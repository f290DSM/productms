package br.gov.sp.cps.fatecararas.productms.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    @NotEmpty(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    private Double price;
    @NotEmpty(message = "Barcode is required")
    @Size(min = 1, max = 13, message = "Barcode must be 13 characters long")
    private String barcode;
}
