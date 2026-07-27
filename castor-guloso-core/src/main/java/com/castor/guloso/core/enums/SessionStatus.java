package com.castor.guloso.core.enums;

/**
 * Status of a daily lunch session.
 * <p>
 * When a session is {@link #OPEN}, new orders can be placed.
 * When {@link #CLOSED}, the kitchen summary has been generated
 * and no further modifications are allowed.
 */
public enum SessionStatus {
    OPEN,
    CLOSED
}
