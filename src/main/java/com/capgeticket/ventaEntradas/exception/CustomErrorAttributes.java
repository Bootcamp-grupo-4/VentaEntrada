package com.capgeticket.ventaEntradas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorAttributes.class);

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    /**
     * Sobrescribe el método `getErrorAttributes` para personalizar los atributos de error devueltos en las respuestas de error.
     * Este método se encarga de modificar y agregar información adicional a los atributos por defecto en caso de que ocurra un error.
     *
     * - Se registra un log cuando se personalizan los atributos de error.
     * - Si el atributo de marca de tiempo ("timestamp") no está presente, se añade el valor actual formateado.
     * - Si el atributo de marca de tiempo está presente, se reformatea usando el formato "dd/MM/yyyy HH:mm:ss".
     * - Elimina el atributo "trace" de la respuesta para evitar la exposición innecesaria del rastro de error.
     * - Añade el atributo "jdk" que contiene la versión de Java que está ejecutando la aplicación.
     * - Añade un atributo adicional "info_adicional" con un mensaje personalizado de error.
     *
     * @param webRequest el objeto de solicitud web que contiene detalles sobre la solicitud que causó el error.
     * @param options las opciones de error que permiten controlar qué detalles se deben incluir en los atributos de error.
     * @return un `Map<String, Object>` con los atributos de error personalizados.
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        logger.info("Personalizando los atributos de error: " + options);

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        Object timestamp = errorAttributes.get("timestamp");
        if (timestamp == null) {
            errorAttributes.put("timestamp", dateFormat.format(new Date()));
        } else {
            errorAttributes.put("timestamp", dateFormat.format((Date) timestamp));
        }

        errorAttributes.remove("trace");

        errorAttributes.put("jdk", System.getProperty("java.version"));
        errorAttributes.put("info_adicional", "Que ficiste ahi ho, que estu nun furrula");

        return errorAttributes;
    }
}