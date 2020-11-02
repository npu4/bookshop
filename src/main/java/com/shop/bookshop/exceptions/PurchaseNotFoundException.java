package com.shop.bookshop.exceptions;

public class PurchaseNotFoundException extends RuntimeException {
    public PurchaseNotFoundException(Long id) {
        super("Could not find purchase with id = " + id);
    }
}