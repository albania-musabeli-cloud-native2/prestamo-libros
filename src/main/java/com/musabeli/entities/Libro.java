package com.musabeli.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer stock;
    private LocalDateTime createdAt;
}
