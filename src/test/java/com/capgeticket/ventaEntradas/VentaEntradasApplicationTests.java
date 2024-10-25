package com.capgeticket.ventaEntradas;

import com.capgeticket.ventaEntradas.controller.VentaEntradasController;
import com.capgeticket.ventaEntradas.dto.BancoDto;
import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.exception.BancoRejectedException;
import com.capgeticket.ventaEntradas.exception.EventoNotFoundException;
import com.capgeticket.ventaEntradas.exception.InestableBankException;
import com.capgeticket.ventaEntradas.feignClients.BancoFeignClient;
import com.capgeticket.ventaEntradas.feignClients.EventoFeignClient;
import com.capgeticket.ventaEntradas.model.Evento;
import com.capgeticket.ventaEntradas.model.VentaEntrada;
import com.capgeticket.ventaEntradas.repository.VentaEntradasRepository;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;
import com.capgeticket.ventaEntradas.service.VentaEntradasService;
import com.capgeticket.ventaEntradas.service.VentaEntradasServiceImpl;
import feign.FeignException;
import feign.Response;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(VentaEntradasController.class)
class VentaEntradasApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VentaEntradasService service;

	@InjectMocks
	private VentaEntradasServiceImpl serviceImpl;

	@MockBean
	private VentaEntradasRepository repository;

	@Mock
	private BancoFeignClient bancoFeignClient;

	@Mock
	private EventoFeignClient eventoFeignClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testValidCompra() {
		BancoDto dto = new BancoDto();
		BancoResponse b = new BancoResponse("timestamp", "200", "",List.of("Compra valida"), dto, "");
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		VentaEntrada repRet = new VentaEntrada(1L, eEntity, venta.getNombreTitular(), venta.getCorreoTitular(), venta.getNumeroTarjeta(), venta.getMesCaducidad(), venta.getYearCaducidad(), venta.getConcepto(), venta.getCantidad(), venta.getFechaCompra());
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(repository.save(any(VentaEntrada.class))).thenReturn(repRet);
		when(eventoFeignClient.getEventoById(evento.getBody().getId())).thenReturn(evento);
		ResponseEntity<BancoResponse> banco = new ResponseEntity<BancoResponse>(b, HttpStatus.OK);
		when(bancoFeignClient.pay(dto)).thenReturn(banco);
		VentaEntradasResponseDto respuesta = serviceImpl.compra(venta);
		assertEquals("Venta confirmada", respuesta.getMensaje());
	}

	@Test
	void testInvalidCompraNullEvento() {
		VentaEntradasDto venta = new VentaEntradasDto();
		assertThrows(IllegalArgumentException.class, () -> {
			serviceImpl.compra(venta);
		});
	}

	@Test
	void testInvalidCompraNullEventoId() {
		VentaEntradasDto venta = new VentaEntradasDto();
		EventoDto e = new EventoDto();
		e.setId(null);
		assertThrows(IllegalArgumentException.class, () -> {
			serviceImpl.compra(venta);
		});
	}

	@Test
	void testValidCompraWithNonExistenEvento() {
		when(eventoFeignClient.getEventoById(1000L)).thenThrow(new EventoNotFoundException("Evento not found"));
		EventoDto eve = new EventoDto(1000L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		EventoNotFoundException thrown = assertThrows(EventoNotFoundException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("Evento not found", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400NoFunds() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No funds"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No funds", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400NoClient() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No client"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No client", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidCard() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("Invalid Card"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("Invalid Card", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidCCV() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No CVV"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No CVV", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidMonth() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No month"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No month", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidYear() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No Year"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No year", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidDate() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("No date"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("No date", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400InvalidName() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("Invalid name"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("Invalid name", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError500() {
		EventoDto eve = new EventoDto(1L, "Evento","Descripcion", LocalDate.now(), BigDecimal.TEN, BigDecimal.TEN, "localidad", "nombreRecinto", "genero",true);
		Evento eEntity = new Evento(eve.getId(), eve.getNombre(), eve.getDescripcion(), eve.getFechaEvento(), eve.getPrecioMinimo(), eve.getPrecioMaximo(), eve.getLocalidad(), eve.getNombreDelRecinto(), eve.getGenero(), eve.getMostrar());
		VentaEntradasDto venta = new VentaEntradasDto(eve, "correo", "nombre", "4444444444444444", 10, 2030, 123, "Emisor");
		ResponseEntity<EventoDto> evento = new ResponseEntity<>(eve,HttpStatus.OK);
		when(eventoFeignClient.getEventoById(1L)).thenReturn(evento);
		when(bancoFeignClient.pay(any(BancoDto.class))).thenThrow(new BancoRejectedException("Bank error"));
		BancoRejectedException thrown = assertThrows(BancoRejectedException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("Bank error", thrown.getMessage());
	}



}
