package org.estore.e_store_order_service.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(){
        super("Entity not found");
    }

    public ResourceNotFoundException(String entity, String field, String value){
        super(entity+" not found with "+field+" "+value);
    }

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
