package org.estore.e_store_order_service.exceptions;

public class UnauthorizeException extends RuntimeException {

    public UnauthorizeException() {
        super("Invalid Request");
    }

    public UnauthorizeException(String msg) {
        super(msg);
    }
}
