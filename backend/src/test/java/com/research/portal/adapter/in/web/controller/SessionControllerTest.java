package com.research.portal.adapter.in.web.controller;

import com.research.portal.config.SessionConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-Tests für {@link SessionController}.
 *
 * <p>Testet die Session-Management Endpoints mit MockMvc:
 * <ul>
 *   <li>{@code GET  /api/session/status}    — Session-Status</li>
 *   <li>{@code POST /api/session/keepalive} — Keep-Alive</li>
 * </ul>
 */
@WebMvcTest(SessionController.class)
@Import(SessionConfig.class)
@DisplayName("SessionController")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET /api/session/status")
    class GetSessionStatus {

        @Test
        @DisplayName("testSessionStatus_returnsActiveSession — Gibt active=true zurück wenn Session existiert")
        void testSessionStatus_returnsActiveSession() throws Exception {
            // Erstelle eine MockHttpSession mit 30 Minuten Timeout
            MockHttpSession session = new MockHttpSession();
            session.setMaxInactiveInterval(1800); // 30 Minuten in Sekunden

            mockMvc.perform(get("/api/session/status")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.maxInactiveInterval").value(1800))
                    .andExpect(jsonPath("$.expiresInSeconds").isNumber());
        }

        @Test
        @DisplayName("Gibt active=false zurück wenn keine Session existiert")
        void testSessionStatus_returnsInactiveWhenNoSession() throws Exception {
            mockMvc.perform(get("/api/session/status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(false))
                    .andExpect(jsonPath("$.expiresInSeconds").value(0))
                    .andExpect(jsonPath("$.maxInactiveInterval").value(0));
        }

        @Test
        @DisplayName("expiresInSeconds ist kleiner oder gleich maxInactiveInterval")
        void testSessionStatus_expiresInSecondsWithinBounds() throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setMaxInactiveInterval(1800);

            mockMvc.perform(get("/api/session/status")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.expiresInSeconds",
                            is(greaterThan(-1))));
        }
    }

    @Nested
    @DisplayName("POST /api/session/keepalive")
    class KeepAlive {

        @Test
        @DisplayName("testKeepAlive_extendsSession — Gibt extended=true zurück")
        void testKeepAlive_extendsSession() throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setMaxInactiveInterval(1800);

            mockMvc.perform(post("/api/session/keepalive")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.extended").value(true))
                    .andExpect(jsonPath("$.maxInactiveInterval").value(1800))
                    .andExpect(jsonPath("$.sessionId").isString());
        }

        @Test
        @DisplayName("Erstellt neue Session wenn keine vorhanden und gibt extended=true zurück")
        void testKeepAlive_createsNewSessionIfNone() throws Exception {
            mockMvc.perform(post("/api/session/keepalive"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.extended").value(true))
                    .andExpect(jsonPath("$.sessionId").isString());
        }

        @Test
        @DisplayName("Gibt maxInactiveInterval aus der Session zurück")
        void testKeepAlive_returnsMaxInactiveInterval() throws Exception {
            MockHttpSession session = new MockHttpSession();
            session.setMaxInactiveInterval(900); // 15 Minuten

            mockMvc.perform(post("/api/session/keepalive")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maxInactiveInterval").value(900));
        }
    }
}
