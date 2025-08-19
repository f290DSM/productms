package br.gov.sp.cps.fatecararas.productms.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.sp.cps.fatecararas.productms.domain.dto.CurrenciesDTO;

@Service
public class UpdateCurrencyService {

    private final RestClient client;

    public UpdateCurrencyService(RestClient client) {
        this.client = client;
    }

    @Scheduled(fixedRate = 500000)
    public void update() throws JsonProcessingException {
        System.out.println("Updating currency");
        CurrenciesDTO response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(CurrenciesDTO.class);
        
        System.out.println(response);
        System.out.println(new ObjectMapper().writeValueAsString(response));
    }

}
