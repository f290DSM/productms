package br.gov.sp.cps.fatecararas.productms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrenciesDTO {
    @JsonProperty("results")
    private ResultDTO result;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultDTO {
        @JsonProperty("currencies")
        private CurrenciesDataDTO data;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrenciesDataDTO {
        @JsonProperty("USD")
        private CurrencyDTO dollar;
        @JsonProperty("EUR")
        private CurrencyDTO euro;
    }
}
