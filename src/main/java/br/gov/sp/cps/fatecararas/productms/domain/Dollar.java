package br.gov.sp.cps.fatecararas.productms.domain;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;

@Entity
@JsonTypeName("USD")
public class Dollar extends Currency {
    
}
