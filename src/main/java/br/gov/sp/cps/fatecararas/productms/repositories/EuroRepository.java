package br.gov.sp.cps.fatecararas.productms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.sp.cps.fatecararas.productms.domain.Euro;

public interface EuroRepository extends JpaRepository<Euro, Long> {
    
}
