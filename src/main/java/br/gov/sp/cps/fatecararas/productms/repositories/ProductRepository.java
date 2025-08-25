package br.gov.sp.cps.fatecararas.productms.repositories;

import br.gov.sp.cps.fatecararas.productms.domain.ProductEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

}
