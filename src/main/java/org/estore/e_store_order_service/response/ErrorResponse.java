package org.estore.e_store_order_service.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorResponse {
    @JsonIgnore
    private Integer status;
    private String error;
    private List<String> suberrors;
}