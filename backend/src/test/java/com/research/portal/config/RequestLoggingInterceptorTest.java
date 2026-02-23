package com.research.portal.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer {@link RequestLoggingInterceptor}.
 *
 * <p>Prueft das korrekte Verhalten des Interceptors:
 * Speicherung der Start-Zeit, Logging-Format und Metriken-Erfassung.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestLoggingInterceptor")
class RequestLoggingInterceptorTest {

    @Mock
    private ApiMetricsCollector metricsCollector;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RequestLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RequestLoggingInterceptor(metricsCollector);
    }

    @Nested
    @DisplayName("preHandle")
    class PreHandle {

        @Test
        @DisplayName("Speichert Start-Zeit als Request-Attribut")
        void shouldStoreStartTimeAsAttribute() {
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            verify(request).setAttribute(
                    eq(RequestLoggingInterceptor.START_TIME_ATTR),
                    argThat(arg -> arg instanceof Long && (Long) arg > 0)
            );
        }

        @Test
        @DisplayName("Gibt immer true zurueck um Verarbeitung fortzusetzen")
        void shouldAlwaysReturnTrue() {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/reports");

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("afterCompletion")
    class AfterCompletion {

        @Test
        @DisplayName("Ruft metricsCollector.recordRequest auf")
        void shouldCallMetricsCollectorRecordRequest() {
            long startTime = System.nanoTime() - 10_000_000; // 10ms vorher
            when(request.getAttribute(RequestLoggingInterceptor.START_TIME_ATTR)).thenReturn(startTime);
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(response.getStatus()).thenReturn(200);

            interceptor.afterCompletion(request, response, new Object(), null);

            verify(metricsCollector).recordRequest(
                    eq("GET"),
                    eq("/api/reports"),
                    eq(200),
                    longThat(duration -> duration >= 0)
            );
        }

        @Test
        @DisplayName("Berechnet Dauer korrekt aus Start-Zeit und aktueller Zeit")
        void shouldCalculateDurationFromStartTime() {
            // Start-Zeit: 50ms in der Vergangenheit
            long startTime = System.nanoTime() - 50_000_000;
            when(request.getAttribute(RequestLoggingInterceptor.START_TIME_ATTR)).thenReturn(startTime);
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(response.getStatus()).thenReturn(200);

            interceptor.afterCompletion(request, response, new Object(), null);

            verify(metricsCollector).recordRequest(
                    anyString(),
                    anyString(),
                    anyInt(),
                    longThat(duration -> duration >= 40 && duration <= 200)
            );
        }

        @Test
        @DisplayName("Tut nichts wenn Start-Zeit nicht gesetzt ist")
        void shouldDoNothingWhenStartTimeNotSet() {
            when(request.getAttribute(RequestLoggingInterceptor.START_TIME_ATTR)).thenReturn(null);

            interceptor.afterCompletion(request, response, new Object(), null);

            verifyNoInteractions(metricsCollector);
        }

        @Test
        @DisplayName("Uebergibt korrekten HTTP-Status an Collector")
        void shouldPassCorrectStatusToCollector() {
            long startTime = System.nanoTime();
            when(request.getAttribute(RequestLoggingInterceptor.START_TIME_ATTR)).thenReturn(startTime);
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/reports");
            when(response.getStatus()).thenReturn(404);

            interceptor.afterCompletion(request, response, new Object(), null);

            verify(metricsCollector).recordRequest(
                    eq("POST"),
                    eq("/api/reports"),
                    eq(404),
                    anyLong()
            );
        }

        @Test
        @DisplayName("Uebergibt korrekten URI an Collector")
        void shouldPassCorrectUriToCollector() {
            long startTime = System.nanoTime();
            when(request.getAttribute(RequestLoggingInterceptor.START_TIME_ATTR)).thenReturn(startTime);
            when(request.getMethod()).thenReturn("DELETE");
            when(request.getRequestURI()).thenReturn("/api/reports/42");
            when(response.getStatus()).thenReturn(204);

            interceptor.afterCompletion(request, response, new Object(), null);

            verify(metricsCollector).recordRequest(
                    eq("DELETE"),
                    eq("/api/reports/42"),
                    eq(204),
                    anyLong()
            );
        }
    }
}
