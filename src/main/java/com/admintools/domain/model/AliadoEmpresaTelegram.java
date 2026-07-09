package com.admintools.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "aliado_empresa_telegram")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliadoEmpresaTelegram {

    @Id
    @Column(name = "aliado_id", nullable = false)
    private UUID aliadoId;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;
}
