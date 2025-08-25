package br.gov.sp.cps.fatecararas.productms.services;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.gov.sp.cps.fatecararas.productms.domain.DollarEntity;
import br.gov.sp.cps.fatecararas.productms.domain.EuroEntity;
import br.gov.sp.cps.fatecararas.productms.domain.dto.CurrenciesDTO;
import br.gov.sp.cps.fatecararas.productms.repositories.DollarRepository;
import br.gov.sp.cps.fatecararas.productms.repositories.EuroRepository;

@Service
public class UpdateCurrencyService {

    private final RestClient client;
    private final DollarRepository dollarRepository;
    private final EuroRepository euroRepository;
    private final ModelMapper modelMapper;

    public UpdateCurrencyService(RestClient client, DollarRepository dollarRepository, EuroRepository euroRepository, ModelMapper modelMapper) {
        this.client = client;
        this.dollarRepository = dollarRepository;
        this.euroRepository = euroRepository;
        this.modelMapper = modelMapper;
    }

    @Scheduled(fixedRate = 500000)
    public void update() throws JsonProcessingException {
        System.out.println("Updating currency");
        CurrenciesDTO response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(CurrenciesDTO.class);

        if (response == null) {
            System.out.println("Response is null");
            return;
        }

        var dollar = response.getResult().getData().getDollar();
        var euro = response.getResult().getData().getEuro();

        // Conversão de DTOs em Entities
        DollarEntity dollarEntity = modelMapper.map(dollar, DollarEntity.class);
        EuroEntity euroEntity = modelMapper.map(euro, EuroEntity.class);

        dollarRepository.save(dollarEntity);
        euroRepository.save(euroEntity);
    }

}
