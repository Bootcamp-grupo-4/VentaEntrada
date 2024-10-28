package com.capgeticket.ventaEntradas.mapper;

import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.model.Evento;

public class EventoMapper {

    /**
     * Un mappeador para la entidad del evento a la hora de guardar la venta
     * Se que suena un poco más complicado pero en mi cabeza tiene sentido (Probablemente pueda hacerlo solo con el id, pero bueno por si acaso)
     * @param body el body de la petición del evento
     * @return devuelve un objeto de tipo Evento
     */
    public static Evento eventoMapper(EventoDto body) {
        Evento e = new Evento();
        e.setId(body.getId());
        e.setNombre(body.getNombre());
        e.setDescripcion(body.getDescripcion());
        e.setFechaEvento(body.getFechaEvento());
        e.setPrecioMaximo(body.getPrecioMaximo());
        e.setPrecioMinimo(body.getPrecioMinimo());
        e.setLocalidad(body.getLocalidad());
        e.setNombreDelRecinto(body.getNombreDelRecinto());
        e.setGenero(body.getGenero());
        e.setMostrar(body.getMostrar());
        return e;
    }

    public static EventoDto eventoDtoMapper(Evento evento) {
        EventoDto eventoDto = new EventoDto();
        eventoDto.setId(evento.getId());
        eventoDto.setNombre(evento.getNombre());
        eventoDto.setDescripcion(evento.getDescripcion());
        eventoDto.setFechaEvento(evento.getFechaEvento());
        eventoDto.setPrecioMaximo(evento.getPrecioMaximo());
        eventoDto.setPrecioMinimo(evento.getPrecioMinimo());
        eventoDto.setLocalidad(evento.getLocalidad());
        eventoDto.setNombreDelRecinto(evento.getNombreDelRecinto());
        eventoDto.setGenero(evento.getGenero());
        eventoDto.setMostrar(evento.getMostrar());
        return eventoDto;
    }
}
