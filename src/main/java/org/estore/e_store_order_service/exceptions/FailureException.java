package org.estore.e_store_order_service.exceptions;

public class FailureException extends RuntimeException{

    public FailureException(){
        super("Something went Wrong");
    }

    public FailureException(String msg){
        super(msg);
    }
}
