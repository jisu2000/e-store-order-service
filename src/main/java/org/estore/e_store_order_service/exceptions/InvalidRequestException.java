package org.estore.e_store_order_service.exceptions;

public class InvalidRequestException extends RuntimeException{
    
    public InvalidRequestException(){
        super("This Request can not be fullfilled");
    }

    public InvalidRequestException(String msg){
        super(msg);
    }
}
