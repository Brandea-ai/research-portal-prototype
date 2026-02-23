package com.research.portal.config;

import com.research.portal.application.service.RateLimitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguration fuer API Rate Limiting.
 *
 * <p>Definiert den {@link RateLimitService} und {@link RateLimitInterceptor}
 * als Beans und registriert den Interceptor fuer alle API-Pfade.
 * Actuator-Endpunkte werden ausgeschlossen.
 *
 * <p>Separater WebMvcConfigurer (nicht in {@link WebMvcInterceptorConfig}),
 * damit die bestehende Konfiguration unveraendert bleibt.
 *
 * <p>Der WebMvcConfigurer wird als Bean zurueckgegeben statt ueber direktes
 * Implementieren des Interfaces, damit @WebMvcTest-Slices diese Konfiguration
 * nicht automatisch laden und bestehende Controller-Tests nicht beeinflusst werden.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Erstellt den RateLimitService als Bean.
     *
     * @return neue Instanz des RateLimitService
     */
    @Bean
    public RateLimitService rateLimitService() {
        return new RateLimitService();
    }

    /**
     * Erstellt den RateLimitInterceptor als Bean.
     *
     * @param rateLimitService der injizierte RateLimitService
     * @return neue Instanz des RateLimitInterceptor
     */
    @Bean
    public RateLimitInterceptor rateLimitInterceptor(RateLimitService rateLimitService) {
        return new RateLimitInterceptor(rateLimitService);
    }

    /**
     * Registriert den RateLimitInterceptor fuer alle API-Pfade.
     * Actuator-Endpunkte und Rate-Limit-eigene Endpunkte werden ausgeschlossen,
     * um Monitoring-Loops und Selbst-Limitierung zu vermeiden.
     *
     * @param interceptor der zu registrierende Interceptor
     * @return WebMvcConfigurer mit Interceptor-Registrierung
     */
    @Bean
    public WebMvcConfigurer rateLimitWebMvcConfigurer(RateLimitInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor)
                        .addPathPatterns("/api/**")
                        .excludePathPatterns("/api/actuator/**")
                        .excludePathPatterns("/api/rate-limit/**");
            }
        };
    }
}
