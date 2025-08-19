package br.gov.sp.cps.fatecararas.productms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.sp.cps.fatecararas.productms.domain.Dollar;

public interface DollarRepository extends JpaRepository<Dollar, Long> {
    
}
