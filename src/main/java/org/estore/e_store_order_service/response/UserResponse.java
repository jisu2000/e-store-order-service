package org.estore.e_store_order_service.response;

import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private List<RoleResponse> roles;
    private String profilePhoto;
}
