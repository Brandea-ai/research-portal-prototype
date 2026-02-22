package com.research.portal.domain.model;

/**
 * Analystenempfehlung für eine Wertschrift.
 * STRONG_BUY und BUY werden im UI mit Accent-Farbe (#38BDF8) dargestellt.
 * SELL und STRONG_SELL mit Rot (#F87171).
 */
public enum Rating {
    STRONG_BUY,
    BUY,
    HOLD,
    SELL,
    STRONG_SELL
}
