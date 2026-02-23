package com.research.portal.config;

import com.research.portal.application.service.RateLimitService;
import com.research.portal.application.service.RateLimitService.RateLimitInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer {@link RateLimitInterceptor}.
 *
 * <p>Prueft das korrekte Verhalten des Interceptors:
 * Request-Durchlass bei Erlaubnis, 429-Response bei Blockierung,
 * Setzen der Rate-Limit-Headers und X-Forwarded-For-Verarbeitung.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitInterceptor")
class RateLimitInterceptorTest {

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitInterceptor(rateLimitService);
    }

    @Nested
    @DisplayName("preHandle")
    class PreHandle {

        @Test
        @DisplayName("Laesst Request durch wenn erlaubt")
        void shouldAllowRequestWhenPermitted() throws Exception {
            when(request.getRemoteAddr()).thenReturn("192.168.1.1");
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn(null);
            when(rateLimitService.determineCategory("GET", "/api/reports")).thenReturn("READ");
            when(rateLimitService.isAllowed("192.168.1.1", "READ")).thenReturn(true);
            when(rateLimitService.getRateLimitInfo("192.168.1.1", "READ"))
                    .thenReturn(new RateLimitInfo(99, 100, System.currentTimeMillis() + 60000));

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Gibt 429 zurueck wenn Limit ueberschritten")
        void shouldReturn429WhenRateLimitExceeded() throws Exception {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            when(request.getRemoteAddr()).thenReturn("10.0.0.1");
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn(null);
            when(rateLimitService.determineCategory("POST", "/api/reports")).thenReturn("WRITE");
            when(rateLimitService.isAllowed("10.0.0.1", "WRITE")).thenReturn(false);
            when(rateLimitService.getRateLimitInfo("10.0.0.1", "WRITE"))
                    .thenReturn(new RateLimitInfo(0, 30, System.currentTimeMillis() + 60000));
            when(response.getWriter()).thenReturn(printWriter);

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isFalse();
            verify(response).setStatus(429);
        }

        @Test
        @DisplayName("Setzt Rate-Limit Response-Headers")
        void shouldSetRateLimitHeaders() throws Exception {
            long resetAt = System.currentTimeMillis() + 60000;
            when(request.getRemoteAddr()).thenReturn("192.168.1.2");
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/analysts");
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn(null);
            when(rateLimitService.determineCategory("GET", "/api/analysts")).thenReturn("READ");
            when(rateLimitService.isAllowed("192.168.1.2", "READ")).thenReturn(true);
            when(rateLimitService.getRateLimitInfo("192.168.1.2", "READ"))
                    .thenReturn(new RateLimitInfo(95, 100, resetAt));

            interceptor.preHandle(request, response, new Object());

            verify(response).setIntHeader(RateLimitInterceptor.HEADER_LIMIT, 100);
            verify(response).setIntHeader(RateLimitInterceptor.HEADER_REMAINING, 95);
            verify(response).setHeader(RateLimitInterceptor.HEADER_RESET, String.valueOf(resetAt / 1000));
        }

        @Test
        @DisplayName("Verwendet X-Forwarded-For Header fuer die Client-IP")
        void shouldUseXForwardedForForClientIp() throws Exception {
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR))
                    .thenReturn("203.0.113.50, 70.41.3.18, 150.172.238.178");
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(rateLimitService.determineCategory("GET", "/api/reports")).thenReturn("READ");
            when(rateLimitService.isAllowed("203.0.113.50", "READ")).thenReturn(true);
            when(rateLimitService.getRateLimitInfo("203.0.113.50", "READ"))
                    .thenReturn(new RateLimitInfo(99, 100, System.currentTimeMillis() + 60000));

            interceptor.preHandle(request, response, new Object());

            verify(rateLimitService).isAllowed("203.0.113.50", "READ");
        }

        @Test
        @DisplayName("Faellt auf RemoteAddr zurueck wenn X-Forwarded-For fehlt")
        void shouldFallbackToRemoteAddrWhenNoForwardedFor() throws Exception {
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(rateLimitService.determineCategory("GET", "/api/reports")).thenReturn("READ");
            when(rateLimitService.isAllowed("127.0.0.1", "READ")).thenReturn(true);
            when(rateLimitService.getRateLimitInfo("127.0.0.1", "READ"))
                    .thenReturn(new RateLimitInfo(99, 100, System.currentTimeMillis() + 60000));

            interceptor.preHandle(request, response, new Object());

            verify(rateLimitService).isAllowed("127.0.0.1", "READ");
        }

        @Test
        @DisplayName("Schreibt JSON-Body bei 429 Response")
        void shouldWriteJsonBodyWhenBlocked() throws Exception {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            when(request.getRemoteAddr()).thenReturn("10.0.0.2");
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/search");
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn(null);
            when(rateLimitService.determineCategory("GET", "/api/search")).thenReturn("SEARCH");
            when(rateLimitService.isAllowed("10.0.0.2", "SEARCH")).thenReturn(false);
            when(rateLimitService.getRateLimitInfo("10.0.0.2", "SEARCH"))
                    .thenReturn(new RateLimitInfo(0, 20, System.currentTimeMillis() + 60000));
            when(response.getWriter()).thenReturn(printWriter);

            interceptor.preHandle(request, response, new Object());

            verify(response).setContentType("application/json");
            String jsonBody = stringWriter.toString();
            assertThat(jsonBody).contains("429");
            assertThat(jsonBody).contains("Too Many Requests");
            assertThat(jsonBody).contains("SEARCH");
        }
    }

    @Nested
    @DisplayName("extractClientIp")
    class ExtractClientIp {

        @Test
        @DisplayName("Extrahiert erste IP aus X-Forwarded-For")
        void shouldExtractFirstIpFromForwardedFor() {
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR))
                    .thenReturn("203.0.113.50, 70.41.3.18");

            String ip = interceptor.extractClientIp(request);

            assertThat(ip).isEqualTo("203.0.113.50");
        }

        @Test
        @DisplayName("Gibt RemoteAddr zurueck wenn X-Forwarded-For leer ist")
        void shouldReturnRemoteAddrWhenForwardedForBlank() {
            when(request.getHeader(RateLimitInterceptor.X_FORWARDED_FOR)).thenReturn("  ");
            when(request.getRemoteAddr()).thenReturn("192.168.1.100");

            String ip = interceptor.extractClientIp(request);

            assertThat(ip).isEqualTo("192.168.1.100");
        }
    }
}
