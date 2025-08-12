package br.gov.sp.cps.fatecararas.productms.repositories;

import br.gov.sp.cps.fatecararas.productms.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
