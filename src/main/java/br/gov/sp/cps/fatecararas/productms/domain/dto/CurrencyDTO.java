package br.gov.sp.cps.fatecararas.productms.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrencyDTO {
    private String name;
    private Double buy;
    private Double sell;
    private Double variation;
}
