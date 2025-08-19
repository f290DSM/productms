# Introdução Micro-Serviços com Spring Boot

## Criando o projeto ProductMS

1. No site [Spring Initializr](https://start.spring.io) crie um novo projeto Spring com base no seu mabiente de desenvolvimento e inclua as dependências necessárias:
    - Spring Web
    - Spring Data JPA
    - H2 Database
    - Lombok
    - Flyway
    - Spring Boot DevTools
    - Spring Boot Actuator

2. Após gerar, baixe o projeto e abra-o no seu IDE.

3. Renomeie o arquivo `application.properties` para `application.yml` e configure as propriedades do Spring Boot para o seu ambiente de desenvolvimento.

```yml
spring:
  application:
    name: producms
  h2:
    console:
      enabled: true
  datasource:
    url: jdbc:h2:mem:product-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: aluno
    password: fatec
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: none
    show-sql: true
  flyway:
    enabled: true

server: 
  port: 8000
```

4. Inicie o projeto com o comando `./gradlew bootRun`.

5. Verifique se o projeto foi iniciado corretamente e se o H2 Console está funcionando. 

```
http://localhost:8000/h2-console
```

6. Crie o pacote domain e adicione ProductEntity.java.
```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name="products")
@Data
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Double price;
    private String barcode;
}

```

7. Crie o pacote repositories e adicione ProductRepository.java.
```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
```

8. Baixe o arquivo `V1__CreateTableProducts.sql` em [gist](https://gist.github.com/BZR4/ee307cdae9219244929726080d98669c) e coloque-o no diretório `src/main/resources/db/migration`.

9. Execute o projeto, acesse o h2-console e verifique se as tabelas foram criadas corretamente.

10. Acesse o H2 e faça uma consulta visualizar os produtos criados.

11. Crie o pacote resources e adicione ProductResource.java.

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductResource {

    private final ProductRepository repository;

    public ProductResource(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ProductEntity>> findAll() {
        final List<ProductEntity> products = repository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ProductEntity getMethodName(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

}
```

12. Faça uma requisição para `http://localhost:8000/products` e verifique se os produtos foram retornados corretamente.

13. Faça uma requisição para `http://localhost:8000/products/1` e verifique se o produto foi retornado corretamente.

## Incluindo a funcionalidade de cambio de moedas

Iremos criar uma funcionalidade que irá converter o preço do produto para a moeda desejada.

1. No pacote domain crie a classe Currency.java.
```java
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public abstract class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Double buy;
    private Double sell;
    private Double variation;

    @CreationTimestamp
    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
}
```
2. Crie as classes Dollar.java e Euro.java.

### Dollar
```java
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;

@Entity
@JsonTypeName("EUR")
public class Euro extends Currency {
    
}
```
### Euro
```java
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;

@Entity
@JsonTypeName("USD")
public class Dollar extends Currency {
    
}
```

3. Altere as configurações para que o Spring possa criar as tabelas de moedas automaticamente, ajustando o arquivo application.yml.
```yml
spring:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  flyway:
    enabled: false
```

4. Crie o pacote config e adicione AppConfig.java.
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
```

5. Crie o pacote services e adicione UpdateCurrencyService.java.
```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UpdateCurrencyService {

    private final RestClient client;

    public UpdateCurrencyService(RestClient client) {
        this.client = client;
    }

    @Scheduled(fixedRate = 5000)
    public void update() {
        System.out.println("Updating currency");
        String response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(String.class);
        System.out.println(response);
    }

}
```

6. Habilitar o agendamento de tarefas na classe ProductmsApplication.java.
```java
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProductmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductmsApplication.class, args);
    }

}
```

7. Teste o Scheduler verficando o log do console. 



