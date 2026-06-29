package com.admintools.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoSolicitudConverter implements AttributeConverter<EstadoSolicitud, String> {

    @Override
    public String convertToDatabaseColumn(EstadoSolicitud attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public EstadoSolicitud convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EstadoSolicitud.valueOf(dbData);
    }
}
