package br.gov.sp.cps.fatecararas.productms.resources;

import br.gov.sp.cps.fatecararas.productms.domain.dto.ProductDTO;
import br.gov.sp.cps.fatecararas.productms.services.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }

    @GetMapping("/description/{description}")
    public Page<ProductDTO> findByDescriptionContaining(@PathVariable("description") String description, Pageable pageable) {
        return productService.findByDescriptionContaining(description, pageable);
    }
}
