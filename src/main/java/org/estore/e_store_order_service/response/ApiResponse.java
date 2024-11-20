package org.estore.e_store_order_service.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private Integer status;
    private boolean success;
    private ErrorResponse error;
    private String timeStamp;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now().toString();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
        this.success = true;
        status = 200;
    }

    public ApiResponse(ErrorResponse errorResponse) {
        this();
        this.error = errorResponse;
        this.success = false;
    }
}
