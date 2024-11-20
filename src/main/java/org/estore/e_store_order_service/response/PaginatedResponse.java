package org.estore.e_store_order_service.response;

import java.util.List;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaginatedResponse {
    
    private List<?> content;

    private Integer currentPage;

    private Integer pageSize;

    private Integer totalItems;

    private Integer currentPageItemsNumber;

    private Integer totalPages;

    private boolean isLastPage;
}
