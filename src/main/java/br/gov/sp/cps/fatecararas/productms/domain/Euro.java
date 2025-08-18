package br.gov.sp.cps.fatecararas.productms.domain;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;

@Entity
@JsonTypeName("EUR")
public class Euro extends Currency {
    
}
