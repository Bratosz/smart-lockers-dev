package pl.bratosz.smartlockers.model.users;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserPermission {
    VIEW_CLOTHES,
    VIEW_ORDERS,
    REQUEST_ORDER,
    CANCEL_ORDER,
    CONFIRM_ORDER,
    ACCEPT_ORDER,
}
