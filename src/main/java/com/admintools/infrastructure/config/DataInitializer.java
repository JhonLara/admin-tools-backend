package com.admintools.infrastructure.config;

import com.admintools.domain.model.*;
import com.admintools.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            JpaEmpresaRepository empresaRepo,
            JpaAliadoRepository aliadoRepo,
            JpaAnalistaRepository analistaRepo,
            JpaHorarioAnalistaRepository horarioRepo,
            JpaUsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (empresaRepo.count() > 0) {
                return;
            }

            // Empresas
            Empresa empresa1 = Empresa.builder()
                    .nombre("UNO+1 INVERSIONES")
                    .estado(EstadoEmpresa.ACTIVA)
                    .build();
            Empresa empresa2 = Empresa.builder()
                    .nombre("ESCALANDO INVERSIONES")
                    .estado(EstadoEmpresa.ACTIVA)
                    .build();
            empresa1 = empresaRepo.save(empresa1);
            empresa2 = empresaRepo.save(empresa2);

            // Aliados de UNO+1 INVERSIONES
            Aliado a1 = Aliado.builder().nombre("BLEST TECNOLOGY").empresa(empresa1).telegramChatId("-5539380516").estado(EstadoAliado.ACTIVO).build();
            Aliado a2 = Aliado.builder().nombre("CREDI PHONE").empresa(empresa1).telegramChatId("-5542679471").estado(EstadoAliado.ACTIVO).build();
            Aliado a3 = Aliado.builder().nombre("HTC").empresa(empresa1).telegramChatId("-5504097691").estado(EstadoAliado.ACTIVO).build();
            Aliado a4 = Aliado.builder().nombre("SUR CEL NARIÑO").empresa(empresa1).telegramChatId("-5355528411").estado(EstadoAliado.ACTIVO).build();
            Aliado a5 = Aliado.builder().nombre("TECNOKELLY").empresa(empresa1).telegramChatId("-5503954638").estado(EstadoAliado.ACTIVO).build();

            // Aliados de ESCALANDO INVERSIONES
            Aliado a6 = Aliado.builder().nombre("ALVARO ANDRES SOLANO").empresa(empresa2).telegramChatId("-5250645120").estado(EstadoAliado.ACTIVO).build();
            Aliado a7 = Aliado.builder().nombre("CELESSENCE STORE").empresa(empresa2).telegramChatId("-5507132468").estado(EstadoAliado.ACTIVO).build();
            Aliado a8 = Aliado.builder().nombre("GB CELL").empresa(empresa2).telegramChatId("-5579370030").estado(EstadoAliado.ACTIVO).build();
            Aliado a9 = Aliado.builder().nombre("JFG COMUNICACIONES").empresa(empresa2).telegramChatId("-5506433015").estado(EstadoAliado.ACTIVO).build();
            Aliado a10 = Aliado.builder().nombre("MIRROR ENERGY S&I S.A.S.").empresa(empresa2).telegramChatId("-5478113912").estado(EstadoAliado.ACTIVO).build();
            Aliado a11 = Aliado.builder().nombre("QIERO").empresa(empresa2).telegramChatId("-5426380900").estado(EstadoAliado.ACTIVO).build();
            Aliado a12 = Aliado.builder().nombre("SMARTPHONE CENTER").empresa(empresa2).telegramChatId("-5339562528").estado(EstadoAliado.ACTIVO).build();
            Aliado a13 = Aliado.builder().nombre("TECNOBRO´S").empresa(empresa2).telegramChatId("-5472340383").estado(EstadoAliado.ACTIVO).build();

            aliadoRepo.save(a1);
            aliadoRepo.save(a2);
            aliadoRepo.save(a3);
            aliadoRepo.save(a4);
            aliadoRepo.save(a5);
            aliadoRepo.save(a6);
            aliadoRepo.save(a7);
            aliadoRepo.save(a8);
            aliadoRepo.save(a9);
            aliadoRepo.save(a10);
            aliadoRepo.save(a11);
            aliadoRepo.save(a12);
            aliadoRepo.save(a13);

            // Analistas
            Analista analista1 = Analista.builder()
                    .nombre("Jeuzabeth Roa Rodriguez")
                    .cedula("41940600")
                    .ordenAsignacion(1)
                    .estado(EstadoAnalista.ACTIVO)
                    .build();
            Analista analista2 = Analista.builder()
                    .nombre("Briyth Reyes")
                    .cedula("1059706656")
                    .ordenAsignacion(2)
                    .estado(EstadoAnalista.ACTIVO)
                    .build();
            Analista analista3 = Analista.builder()
                    .nombre("Manuela Bedoya Seguro")
                    .cedula("1004717922")
                    .ordenAsignacion(3)
                    .estado(EstadoAnalista.ACTIVO)
                    .build();

            analista1 = analistaRepo.save(analista1);
            analista2 = analistaRepo.save(analista2);
            analista3 = analistaRepo.save(analista3);

            // Horarios de analistas (Lunes a Viernes, 08:00 - 17:00)
            DiaSemana[] diasLaborales = {DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES, DiaSemana.VIERNES};
            for (Analista analista : List.of(analista1, analista2, analista3)) {
                for (DiaSemana dia : diasLaborales) {
                    HorarioAnalista horario = HorarioAnalista.builder()
                            .analista(analista)
                            .diaSemana(dia)
                            .horaInicio(java.time.LocalTime.of(8, 0))
                            .horaFin(java.time.LocalTime.of(17, 0))
                            .activo(true)
                            .build();
                    horarioRepo.save(horario);
                }
            }

            // Usuarios de prueba
            Usuario vendedor = Usuario.builder()
                    .username("vendedor")
                    .password(passwordEncoder.encode("Adm1n$2026#"))
                    .nombre("Vendedor Demo")
                    .rol(Rol.VENDEDOR)
                    .build();
            Usuario jroa = Usuario.builder()
                    .username("jroa")
                    .password(passwordEncoder.encode("41940600"))
                    .nombre("Jeuzabeth Roa Rodriguez")
                    .rol(Rol.ANALISTA)
                    .analistaId(analista1.getId())
                    .build();
            Usuario breyes = Usuario.builder()
                    .username("breyes")
                    .password(passwordEncoder.encode("1059706656"))
                    .nombre("Briyth Reyes")
                    .rol(Rol.ANALISTA)
                    .analistaId(analista2.getId())
                    .build();
            Usuario mbedoya = Usuario.builder()
                    .username("mbedoya")
                    .password(passwordEncoder.encode("1004717922"))
                    .nombre("Manuela Bedoya Seguro")
                    .rol(Rol.ANALISTA)
                    .analistaId(analista3.getId())
                    .build();
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Adm1n$2026#"))
                    .nombre("Administrador Demo")
                    .rol(Rol.ADMINISTRADOR)
                    .build();
            Usuario superAdmin = Usuario.builder()
                    .username("superadmin")
                    .password(passwordEncoder.encode("Adm1n$2026#"))
                    .nombre("Super Administrador")
                    .rol(Rol.SUPER_ADMIN)
                    .build();

            Usuario jlara = Usuario.builder()
                    .username("jlara")
                    .password(passwordEncoder.encode("Adm1n$2026#"))
                    .nombre("Jhon Lara")
                    .rol(Rol.SUPER_ADMIN)
                    .build();
            Usuario jzapata = Usuario.builder()
                    .username("jzapata")
                    .password(passwordEncoder.encode("Adm1n$2026#"))
                    .nombre("Juan Zapata")
                    .rol(Rol.SUPER_ADMIN)
                    .build();

            usuarioRepo.save(vendedor);
            usuarioRepo.save(jroa);
            usuarioRepo.save(breyes);
            usuarioRepo.save(mbedoya);
            usuarioRepo.save(admin);
            usuarioRepo.save(superAdmin);
            usuarioRepo.save(jlara);
            usuarioRepo.save(jzapata);
        };
    }
}
