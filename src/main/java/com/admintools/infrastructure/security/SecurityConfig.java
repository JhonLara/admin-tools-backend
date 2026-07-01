package com.admintools.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/version").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Lectura de datos de referencia y dashboard -> cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/empresas", "/api/empresas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/aliados", "/api/aliados/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/analistas", "/api/analistas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes", "/api/solicitudes/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/historial-notificaciones", "/api/historial-notificaciones/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/horarios-analistas", "/api/horarios-analistas/**").authenticated()

                        // Gestión de usuarios: solo super admin
                        .requestMatchers("/api/usuarios", "/api/usuarios/**").hasAnyAuthority("ROLE_SUPER_ADMIN")

                        // Vendedor: crear solicitudes y ver sus solicitudes
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes/mis-solicitudes-vendedor").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/firma-recibida").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/revisado").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")

                        // Analista: finalizar, notificar, rechazar y aprobar
                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/finalizar").hasAnyAuthority("ROLE_ANALISTA", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/rechazar").hasAnyAuthority("ROLE_ANALISTA", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/aprobar").hasAnyAuthority("ROLE_ANALISTA", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes/*/notificar-observacion").hasAnyAuthority("ROLE_ANALISTA", "ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes/mis-solicitudes").authenticated()

                        // Super admin: eliminar solicitudes, ver sesiones y testear
                        .requestMatchers(HttpMethod.DELETE, "/api/solicitudes/*").hasAnyAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/sesiones", "/api/sesiones/**").hasAnyAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/test/**").hasAnyAuthority("ROLE_SUPER_ADMIN")

                        // Notificación de despliegue
                        .requestMatchers("/api/admin/despliegue/**").hasAnyAuthority("ROLE_SUPER_ADMIN")

                        // Configuración de grupos Telegram por aliado+empresa
                        .requestMatchers("/api/aliado-empresa-telegram").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")

                        // Gestión de entidades: solo admin y super admin
                        .requestMatchers(HttpMethod.POST, "/api/empresas", "/api/aliados", "/api/analistas", "/api/horarios-analistas").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/empresas/**", "/api/aliados/**", "/api/analistas/**", "/api/horarios-analistas/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/empresas/**", "/api/aliados/**", "/api/analistas/**", "/api/horarios-analistas/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/empresas/**", "/api/aliados/**", "/api/analistas/**", "/api/horarios-analistas/**").hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_SUPER_ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
