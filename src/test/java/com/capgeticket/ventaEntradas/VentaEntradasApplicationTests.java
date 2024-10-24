package com.capgeticket.ventaEntradas;

import com.capgeticket.ventaEntradas.controller.VentaEntradasController;
import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.exception.EventoNotFoundException;
import com.capgeticket.ventaEntradas.feignClients.BancoFeignClient;
import com.capgeticket.ventaEntradas.feignClients.EventoFeignClient;
import com.capgeticket.ventaEntradas.repository.VentaEntradasRepository;
import com.capgeticket.ventaEntradas.service.VentaEntradasService;
import com.capgeticket.ventaEntradas.service.VentaEntradasServiceImpl;
import feign.FeignException;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@WebMvcTest(VentaEntradasController.class)
class VentaEntradasApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VentaEntradasService service;

	@Autowired
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

	}

	@Test
	void testInvalidCompra() {

	}

	@Test
	void testValidCompraWithNonExistenEvento() {
		when(eventoFeignClient.getEventoById(1000L)).thenThrow(new EventoNotFoundException("Evento not found"));
		VentaEntradasDto venta = new VentaEntradasDto();
		EventoDto e = new EventoDto();
		e.setId(1000L);
		venta.setEvento(e);
		EventoNotFoundException thrown = assertThrows(EventoNotFoundException.class, () -> {
			serviceImpl.compra(venta);
		});
		assertEquals("Evento not found", thrown.getMessage());
	}

	@Test
	void testValidCompraWithBankError400NoFunds() {

	}

	@Test
	void testValidCompraWithBankError400NoClient() {

	}

	@Test
	void testValidCompraWithBankError400InvalidCard() {

	}

	@Test
	void testValidCompraWithBankError400InvalidCCV() {

	}

	@Test
	void testValidCompraWithBankError400InvalidMonth() {

	}

	@Test
	void testValidCompraWithBankError400InvalidYear() {

	}

	@Test
	void testValidCompraWithBankError400InvalidDate() {

	}

	@Test
	void testValidCompraWithBankError400InvalidName() {

	}

	@Test
	void testValidCompraWithBankError500() {

	}



}
