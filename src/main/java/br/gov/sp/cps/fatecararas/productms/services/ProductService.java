package br.gov.sp.cps.fatecararas.productms.services;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.sp.cps.fatecararas.productms.domain.DollarEntity;
import br.gov.sp.cps.fatecararas.productms.domain.ProductEntity;
import br.gov.sp.cps.fatecararas.productms.domain.dto.ProductDTO;
import br.gov.sp.cps.fatecararas.productms.repositories.DollarRepository;
import br.gov.sp.cps.fatecararas.productms.repositories.ProductRepository;
import org.springframework.web.client.RestClient;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final DollarRepository dollarRepository;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

    public ProductService(ProductRepository repository, DollarRepository dollarRepository, ModelMapper modelMapper, RestClient restClient) {
        this.repository = repository;
        this.dollarRepository = dollarRepository;
        this.modelMapper = modelMapper;
        this.restClient = restClient;
    }

    public ProductDTO findById(Long id) {
        final ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        //TODO: Incluir requisição para uma nova API - CambioAPI
        final DollarEntity dollar = dollarRepository.findFirstByOrderByDateDesc();

        if (dollar == null) {
            throw new RuntimeException("Dollar not found");
        }

        product.setPrice(product.getPrice() * dollar.getSell());

        return modelMapper.map(product, ProductDTO.class);
    }

    public Page<ProductDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
        .map(product -> modelMapper.map(product, ProductDTO.class));
    }

    public Page<ProductDTO> findByDescriptionContaining(String description, Pageable pageable) {
        return repository.findByDescriptionContainingIgnoreCase(description, pageable)
        .map(product -> modelMapper.map(product, ProductDTO.class));
    }
}
