package com.research.portal.config;

import com.research.portal.application.service.RateLimitService;
import com.research.portal.application.service.RateLimitService.RateLimitInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP-Interceptor fuer API Rate Limiting.
 *
 * <p>Prueft vor jeder Controller-Verarbeitung, ob der Client sein
 * Request-Limit ueberschritten hat. Bei Ueberschreitung wird ein
 * 429 Too Many Requests zurueckgegeben.
 *
 * <p>Setzt immer die Rate-Limit-Response-Header:
 * <ul>
 *   <li>X-RateLimit-Limit: Maximale Requests pro Zeitfenster</li>
 *   <li>X-RateLimit-Remaining: Verbleibende Requests</li>
 *   <li>X-RateLimit-Reset: Reset-Zeitpunkt (Epoch-Sekunden)</li>
 * </ul>
 *
 * <p>Die Client-IP wird aus dem X-Forwarded-For Header extrahiert,
 * falls vorhanden (Reverse-Proxy-Szenario), ansonsten aus der
 * direkten Verbindung via {@code getRemoteAddr()}.
 *
 * <p>Wird als Bean in {@link RateLimitConfig} registriert
 * (nicht als @Component, um WebMvcTest-Slices nicht zu beeinflussen).
 */
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    /** Header-Name fuer die maximale Anzahl Requests pro Zeitfenster. */
    static final String HEADER_LIMIT = "X-RateLimit-Limit";

    /** Header-Name fuer die verbleibenden Requests. */
    static final String HEADER_REMAINING = "X-RateLimit-Remaining";

    /** Header-Name fuer den Reset-Zeitpunkt (Epoch-Sekunden). */
    static final String HEADER_RESET = "X-RateLimit-Reset";

    /** Header fuer die Client-IP hinter einem Reverse Proxy. */
    static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final RateLimitService rateLimitService;

    /**
     * Erstellt einen neuen RateLimitInterceptor.
     *
     * @param rateLimitService der Service fuer Rate-Limit-Pruefungen
     */
    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Wird vor der Controller-Verarbeitung aufgerufen.
     *
     * <p>Extrahiert die Client-IP, bestimmt die Kategorie, prueft das Rate-Limit
     * und setzt die entsprechenden Response-Header.
     * Bei Ueberschreitung wird ein 429-Status mit JSON-Body zurueckgegeben.
     *
     * @return true wenn der Request fortgesetzt werden darf, false bei Limit-Ueberschreitung
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String clientIp = extractClientIp(request);
        String category = rateLimitService.determineCategory(
                request.getMethod(), request.getRequestURI());

        boolean allowed = rateLimitService.isAllowed(clientIp, category);

        // Rate-Limit-Info fuer Response-Headers holen
        RateLimitInfo info = rateLimitService.getRateLimitInfo(clientIp, category);
        response.setIntHeader(HEADER_LIMIT, info.limit());
        response.setIntHeader(HEADER_REMAINING, info.remaining());
        response.setHeader(HEADER_RESET, String.valueOf(info.resetAtEpochMs() / 1000));

        if (!allowed) {
            log.warn("Rate limit ueberschritten: IP={}, Kategorie={}", clientIp, category);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(buildErrorJson(clientIp, category, info));
            return false;
        }

        return true;
    }

    /**
     * Extrahiert die Client-IP aus dem Request.
     * Beruecksichtigt den X-Forwarded-For Header fuer Reverse-Proxy-Szenarien.
     *
     * @param request der HTTP-Request
     * @return die Client-IP-Adresse
     */
    String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // Erster Eintrag ist die urspruengliche Client-IP
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String buildErrorJson(String clientIp, String category, RateLimitInfo info) {
        return String.format(
                "{\"status\":429,"
                        + "\"error\":\"Too Many Requests\","
                        + "\"message\":\"Rate limit ueberschritten fuer Kategorie %s. "
                        + "Bitte warten Sie bis zum Reset.\","
                        + "\"clientIp\":\"%s\","
                        + "\"category\":\"%s\","
                        + "\"limit\":%d,"
                        + "\"remaining\":%d,"
                        + "\"resetAtEpochMs\":%d}",
                category, clientIp, category, info.limit(), info.remaining(), info.resetAtEpochMs());
    }
}
