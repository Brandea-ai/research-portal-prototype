package com.research.portal.domain.model;

/**
 * Anlageklasse einer Wertschrift.
 * Bei ZKB: hauptsächlich EQUITY (Aktien) und FIXED_INCOME (Anleihen).
 */
public enum AssetClass {
    EQUITY,
    FIXED_INCOME,
    DERIVATIVES,
    MACRO
}
