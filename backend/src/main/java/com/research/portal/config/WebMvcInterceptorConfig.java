package com.research.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguration fuer HTTP-Interceptoren.
 *
 * <p>Definiert den {@link RequestLoggingInterceptor} und den {@link ApiMetricsCollector}
 * als Beans und registriert den Interceptor fuer alle API-Pfade.
 * Actuator-Endpunkte werden bewusst ausgeschlossen, um Monitoring-Loops
 * zu vermeiden (Health-Checks wuerden sonst die Metriken verfaelschen).
 *
 * <p>Der WebMvcConfigurer wird als Bean zurueckgegeben statt ueber direktes
 * Implementieren des Interfaces, damit @WebMvcTest-Slices diese Konfiguration
 * nicht automatisch laden und bestehende Controller-Tests nicht beeinflusst werden.
 */
@Configuration
public class WebMvcInterceptorConfig {

    @Bean
    public ApiMetricsCollector apiMetricsCollector() {
        return new ApiMetricsCollector();
    }

    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor(ApiMetricsCollector metricsCollector) {
        return new RequestLoggingInterceptor(metricsCollector);
    }

    @Bean
    public WebMvcConfigurer requestLoggingWebMvcConfigurer(RequestLoggingInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor)
                        .addPathPatterns("/api/**")
                        .excludePathPatterns("/api/actuator/**");
            }
        };
    }
}
