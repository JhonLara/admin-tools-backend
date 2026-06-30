package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "aliado_empresa_telegram")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AliadoEmpresaTelegram.AliadoEmpresaTelegramId.class)
public class AliadoEmpresaTelegram {

    @Id
    @Column(name = "aliado_id", nullable = false)
    private UUID aliadoId;

    @Id
    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliadoEmpresaTelegramId implements Serializable {
        private UUID aliadoId;
        private UUID empresaId;
    }
}
