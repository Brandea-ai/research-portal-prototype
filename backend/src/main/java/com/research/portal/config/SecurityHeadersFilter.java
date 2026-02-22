package com.research.portal.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet-Filter der Security-relevante HTTP-Response-Header setzt.
 *
 * <p>Dieser Filter wird auf jede HTTP-Response angewendet und setzt die
 * folgenden Security-Header zur Absicherung gegen bekannte Web-Angriffe:
 * <ul>
 *   <li>X-Content-Type-Options — verhindert MIME-Sniffing</li>
 *   <li>X-Frame-Options — verhindert Clickjacking via iframes</li>
 *   <li>X-XSS-Protection — aktiviert Browser XSS-Schutz (Legacy)</li>
 *   <li>Strict-Transport-Security — erzwingt HTTPS (HSTS)</li>
 *   <li>Content-Security-Policy — definiert erlaubte Inhaltsquellen</li>
 *   <li>Referrer-Policy — steuert Referrer-Informationen</li>
 *   <li>Permissions-Policy — deaktiviert Browser-Features (Kamera etc.)</li>
 *   <li>Cache-Control — verhindert Caching von API-Responses</li>
 * </ul>
 *
 * <p>FINMA-Kontext: Security Headers sind Teil der technischen Schutzmaßnahmen
 * für regulierte Finanzanwendungen (FINMA-Rundschreiben 2023/1).
 */
@Component
public class SecurityHeadersFilter implements Filter {

    private static final String API_PATH_PREFIX = "/api/";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest
                && response instanceof HttpServletResponse httpResponse) {

            setSecurityHeaders(httpRequest, httpResponse);
        }

        chain.doFilter(request, response);
    }

    /**
     * Setzt alle Security-Header auf die HTTP-Response.
     *
     * @param request  die HTTP-Anfrage (für Pfad-Prüfung)
     * @param response die HTTP-Response auf der die Header gesetzt werden
     */
    private void setSecurityHeaders(HttpServletRequest request,
                                    HttpServletResponse response) {
        // Verhindert MIME-Type-Sniffing durch den Browser
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Verhindert das Einbetten der Seite in iframes (Clickjacking-Schutz)
        response.setHeader("X-Frame-Options", "DENY");

        // Aktiviert den integrierten XSS-Filter des Browsers (Legacy-Support)
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // HTTP Strict Transport Security: erzwingt HTTPS für 1 Jahr inkl. Subdomains
        response.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");

        // Content Security Policy: definiert erlaubte Quellen für Inhalte
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; "
                + "script-src 'self'; "
                + "style-src 'self' 'unsafe-inline'; "
                + "img-src 'self' data:; "
                + "font-src 'self' https://fonts.gstatic.com");

        // Referrer-Policy: sendet Referrer nur bei gleichem oder sicherem Origin
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions-Policy: deaktiviert Kamera, Mikrofon und Geolocation
        response.setHeader("Permissions-Policy",
                "camera=(), microphone=(), geolocation=()");

        // Cache-Control: nur für API-Pfade (API-Responses dürfen nicht gecacht werden)
        if (isApiRequest(request)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }
    }

    /**
     * Prüft ob es sich um eine API-Anfrage handelt.
     *
     * @param request die HTTP-Anfrage
     * @return true wenn der Pfad mit /api/ beginnt
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.startsWith(API_PATH_PREFIX);
    }
}
