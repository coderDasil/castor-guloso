package com.castor.guloso.core.enums;

/**
 * Functional classification of a tag for filtering and UI grouping.
 */
public enum TagType {
    /** Client demographics / workplace segment (e.g., construction, hospital, office) */
    GROUP,
    /** Dietary restrictions and allergies (e.g., gluten-free, diabetic, shrimp allergy) */
    DIETARY,
    /** Financial flags (e.g., late payer, pending invoice) */
    FINANCIAL,
    /** Operational status flags (e.g., blacklisted, vip, trial) */
    STATUS
}
