package com.admintools.application.usecase;

import com.admintools.application.dto.HistorialNotificacionResponse;
import com.admintools.domain.model.HistorialNotificacion;
import com.admintools.domain.port.HistorialNotificacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistorialNotificacionUseCase {

    private final HistorialNotificacionRepositoryPort historialRepository;

    public List<HistorialNotificacionResponse> listar() {
        return historialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HistorialNotificacionResponse mapToResponse(HistorialNotificacion h) {
        return HistorialNotificacionResponse.builder()
                .id(h.getId())
                .solicitudId(h.getSolicitud().getId())
                .cedulaCliente(h.getSolicitud().getCedulaCliente())
                .nombreAliado(h.getSolicitud().getAliado().getNombre())
                .canal(h.getCanal())
                .origen(h.getOrigen())
                .destino(h.getDestino())
                .mensajeEnviado(h.getMensajeEnviado())
                .estadoEnvio(h.getEstadoEnvio())
                .respuestaIntegracion(h.getRespuestaIntegracion())
                .fechaEnvio(h.getFechaEnvio())
                .build();
    }
}
