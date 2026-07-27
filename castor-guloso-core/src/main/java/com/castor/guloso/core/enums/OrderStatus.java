package com.castor.guloso.core.enums;

/**
 * Lifecycle status of an individual order within a daily session.
 */
public enum OrderStatus {
    /** Message sent, awaiting client reply. */
    PENDING,
    /** Client has replied with their lunch choice. */
    CONFIRMED,
    /** No reply received by session cutoff. */
    NO_REPLY
}
