package com.shop.bookshop.etities;

public enum Status {
    IN_PROGRESS,
    PAID,
    COMPLETED,
    CANCELLED;

    public static boolean valid(Status currentStatus, Status newStatus) {
        if (currentStatus == IN_PROGRESS) {
            return (newStatus == PAID || newStatus == CANCELLED);
        } else if (currentStatus == PAID) {
            return (newStatus != IN_PROGRESS);
        } else {
            return false;
        }
    }
}
