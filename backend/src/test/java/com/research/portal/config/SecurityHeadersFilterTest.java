package com.research.portal.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für {@link SecurityHeadersFilter}.
 *
 * <p>Prüft, dass alle Security-Header korrekt auf HTTP-Responses gesetzt werden
 * und Cache-Control nur für API-Pfade gilt.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityHeadersFilter")
class SecurityHeadersFilterTest {

    private SecurityHeadersFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new SecurityHeadersFilter();
    }

    @Nested
    @DisplayName("testSecurityHeaders_areSet")
    class SecurityHeadersAreSet {

        @Test
        @DisplayName("Setzt X-Content-Type-Options: nosniff auf jede Response")
        void testSecurityHeaders_areSet_xContentTypeOptions() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("X-Content-Type-Options", "nosniff");
        }

        @Test
        @DisplayName("Setzt X-Frame-Options: DENY auf jede Response")
        void testSecurityHeaders_areSet_xFrameOptions() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("X-Frame-Options", "DENY");
        }

        @Test
        @DisplayName("Setzt X-XSS-Protection: 1; mode=block auf jede Response")
        void testSecurityHeaders_areSet_xXssProtection() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("X-XSS-Protection", "1; mode=block");
        }

        @Test
        @DisplayName("Setzt Strict-Transport-Security mit 1 Jahr und includeSubDomains")
        void testSecurityHeaders_areSet_strictTransportSecurity() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains");
        }

        @Test
        @DisplayName("Setzt Content-Security-Policy mit erlaubten Quellen")
        void testSecurityHeaders_areSet_contentSecurityPolicy() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader(
                    eq("Content-Security-Policy"),
                    contains("default-src 'self'")
            );
            verify(response).setHeader(
                    eq("Content-Security-Policy"),
                    contains("script-src 'self'")
            );
            verify(response).setHeader(
                    eq("Content-Security-Policy"),
                    contains("font-src 'self' https://fonts.gstatic.com")
            );
        }

        @Test
        @DisplayName("Setzt Referrer-Policy: strict-origin-when-cross-origin")
        void testSecurityHeaders_areSet_referrerPolicy() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Referrer-Policy",
                    "strict-origin-when-cross-origin");
        }

        @Test
        @DisplayName("Setzt Permissions-Policy mit deaktivierten Browser-Features")
        void testSecurityHeaders_areSet_permissionsPolicy() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Permissions-Policy",
                    "camera=(), microphone=(), geolocation=()");
        }

        @Test
        @DisplayName("Ruft FilterChain.doFilter nach dem Setzen der Header auf")
        void testSecurityHeaders_areSet_filterChainContinues() throws Exception {
            when(request.getRequestURI()).thenReturn("/some/path");

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Alle 7 Security-Header werden auf jede Response gesetzt (ohne Cache-Control)")
        void testSecurityHeaders_areSet_allSevenHeadersOnNonApiPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("X-Content-Type-Options", "nosniff");
            verify(response).setHeader("X-Frame-Options", "DENY");
            verify(response).setHeader("X-XSS-Protection", "1; mode=block");
            verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            verify(response).setHeader(eq("Content-Security-Policy"), anyString());
            verify(response).setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            verify(response).setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

            // Cache-Control darf NICHT für Nicht-API-Pfade gesetzt werden
            verify(response, never()).setHeader(eq("Cache-Control"), anyString());
        }
    }

    @Nested
    @DisplayName("testCacheControl_onlyForApiPaths")
    class CacheControlOnlyForApiPaths {

        @Test
        @DisplayName("Cache-Control wird für /api/ Pfade gesetzt")
        void testCacheControl_onlyForApiPaths_apiPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/api/reports");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }

        @Test
        @DisplayName("Cache-Control wird für /api/session/status gesetzt")
        void testCacheControl_onlyForApiPaths_sessionApiPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/api/session/status");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }

        @Test
        @DisplayName("Cache-Control wird NICHT für statische Ressourcen gesetzt")
        void testCacheControl_onlyForApiPaths_staticPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/static/main.js");

            filter.doFilter(request, response, filterChain);

            verify(response, never()).setHeader(eq("Cache-Control"), anyString());
        }

        @Test
        @DisplayName("Cache-Control wird NICHT für Swagger-UI gesetzt")
        void testCacheControl_onlyForApiPaths_swaggerPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

            filter.doFilter(request, response, filterChain);

            verify(response, never()).setHeader(eq("Cache-Control"), anyString());
        }

        @Test
        @DisplayName("Cache-Control wird NICHT für Root-Pfad gesetzt")
        void testCacheControl_onlyForApiPaths_rootPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/");

            filter.doFilter(request, response, filterChain);

            verify(response, never()).setHeader(eq("Cache-Control"), anyString());
        }

        @Test
        @DisplayName("Cache-Control wird für alle API-Unterpfade gesetzt")
        void testCacheControl_onlyForApiPaths_deepApiPath() throws Exception {
            when(request.getRequestURI()).thenReturn("/api/export/reports/csv");

            filter.doFilter(request, response, filterChain);

            verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }
    }
}
