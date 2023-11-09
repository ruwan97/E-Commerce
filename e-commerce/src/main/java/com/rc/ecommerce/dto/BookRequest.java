package com.rc.ecommerce.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    private String author;
    private String isbn;
}
