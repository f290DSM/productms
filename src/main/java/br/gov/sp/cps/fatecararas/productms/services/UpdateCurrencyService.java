package br.gov.sp.cps.fatecararas.productms.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UpdateCurrencyService {

    private final RestClient client;

    public UpdateCurrencyService(RestClient client) {
        this.client = client;
    }

    @Scheduled(fixedRate = 5000)
    public void update() {
        System.out.println("Updating currency");
        String response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(String.class);
        System.out.println(response);
    }

}
