package com.research.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für HTTP-Session-Management.
 *
 * <p>Konfiguriert den Session-Timeout auf 30 Minuten (Standard).
 * Der Timeout ist über {@code app.session.timeout-minutes} in der
 * {@code application.yml} konfigurierbar.
 *
 * <p>FINMA-Kontext: Automatischer Session-Timeout ist eine Pflichtanforderung
 * für regulierte Finanzanwendungen gemäss FINMA-Rundschreiben 2023/1.
 * Inaktive Sessions müssen nach maximal 30 Minuten ungültig werden.
 */
@Configuration
public class SessionConfig {

    /**
     * Standard Session-Timeout in Minuten.
     * Konfigurierbar via {@code app.session.timeout-minutes} in application.yml.
     */
    private int timeoutMinutes = 30;

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    /**
     * Registriert einen {@link ServletContextInitializer} der den Session-Timeout
     * auf den konfigurierten Wert setzt.
     *
     * @return ServletContextInitializer mit Session-Timeout-Konfiguration
     */
    @Bean
    public ServletContextInitializer sessionTimeoutInitializer() {
        return servletContext -> {
            int timeoutSeconds = timeoutMinutes * 60;
            servletContext.setSessionTimeout(timeoutSeconds / 60); // Servlet API: Minuten
        };
    }
}
