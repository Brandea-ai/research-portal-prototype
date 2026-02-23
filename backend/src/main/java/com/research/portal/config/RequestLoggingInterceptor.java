package com.research.portal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP-Request-Logging-Interceptor fuer alle API-Endpunkte.
 *
 * <p>Loggt jede eingehende Anfrage mit Methode und URI beim Start,
 * sowie Statuscode und Verarbeitungsdauer nach Abschluss.
 * Langsame Anfragen (ueber 500ms) werden auf WARN-Level geloggt.
 *
 * <p>Demonstriert Observability-Kompetenz: strukturiertes Request-Logging
 * und Performance-Monitoring als Grundlage fuer produktionsreifes APM.
 *
 * <p>Wird als Bean in {@link WebMvcInterceptorConfig} registriert
 * (nicht als @Component, um WebMvcTest-Slices nicht zu beeinflussen).
 *
 * Log-Format:
 * <pre>
 *   --&gt; GET /api/reports
 *   &lt;-- 200 GET /api/reports [42ms]
 * </pre>
 *
 * Bei langsamen Requests (ueber 500ms):
 * <pre>
 *   WARN: &lt;-- 200 GET /api/reports [1250ms] SLOW REQUEST
 * </pre>
 */
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    /** Request-Attribut-Key fuer die Start-Zeit in Nanosekunden. */
    static final String START_TIME_ATTR = "requestStartTime";

    /** Schwellenwert in Millisekunden, ab dem ein Request als langsam gilt. */
    static final long SLOW_REQUEST_THRESHOLD_MS = 500;

    private final ApiMetricsCollector metricsCollector;

    public RequestLoggingInterceptor(ApiMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    /**
     * Wird vor der Controller-Verarbeitung aufgerufen.
     * Speichert die aktuelle Systemzeit als Request-Attribut und loggt den eingehenden Request.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute(START_TIME_ATTR, System.nanoTime());
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    /**
     * Wird nach vollstaendiger Verarbeitung (inkl. View-Rendering) aufgerufen.
     * Berechnet die Verarbeitungsdauer, loggt das Ergebnis und uebergibt die Metrik
     * an den {@link ApiMetricsCollector}.
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime == null) {
            return;
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (durationMs > SLOW_REQUEST_THRESHOLD_MS) {
            log.warn("<-- {} {} {} [{}ms] SLOW REQUEST", status, method, uri, durationMs);
        } else {
            log.info("<-- {} {} {} [{}ms]", status, method, uri, durationMs);
        }

        metricsCollector.recordRequest(method, uri, status, durationMs);
    }
}
