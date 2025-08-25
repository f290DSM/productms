package br.gov.sp.cps.fatecararas.productms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.sp.cps.fatecararas.productms.domain.EuroEntity;

@Repository
public interface EuroRepository extends JpaRepository<EuroEntity, Long> {

}
