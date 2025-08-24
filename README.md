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

    @Scheduled(fixedRate = 600000)
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

7. Teste o Scheduler verficando o log do console, após encerrar a execução corrente. 
```shell
./gradlew bootRun
```

8. Observe que o scheduler foi executado e a cotação de moedas foi exibida no console.

```shell
Updating currency
{"by":"default","valid_key":false,"results":{"currencies":{"source":"BRL","USD":{"name":"Dollar","buy":5.4227,"sell":5.4222,"variation":0.0},"EUR":{"name":"Euro","buy":6.353,"sell":6.3573,"variation":0.0},"GBP":{"name":"Pound Sterling","buy":7.3346,"sell":null,"variation":0.0},"ARS":{"name":"Argentine Peso","buy":0.0039,"sell":null,"variation":0.0},"CAD":{"name":"Canadian Dollar","buy":3.9212,"sell":null,"variation":0.0},"AUD":{"name":"Australian Dollar","buy":3.5173,"sell":null,"variation":0.0},"JPY":{"name":"Japanese Yen","buy":0.0368,"sell":null,"variation":0.0},"CNY":{"name":"Renminbi","buy":0.7564,"sell":null,"variation":0.0},"BTC":{"name":"Bitcoin","buy":657801.145,"sell":657801.145,"variation":-2.123}},"stocks":{"IBOVESPA":{"name":"BM\u0026F BOVESPA","location":"Sao Paulo, Brazil","points":137968.16,"variation":2.57},"IFIX":{"name":"Índice de Fundos de Investimentos Imobiliários B3","location":"Sao Paulo, Brazil","points":3430.09,"variation":0.13},"NASDAQ":{"name":"NASDAQ Stock Market","location":"New York City, United States","points":21496.54,"variation":1.88},"DOWJONES":{"name":"Dow Jones Industrial Average","location":"New York City, United States","points":45631.74,"variation":1.89},"CAC":{"name":"CAC 40","location":"Paris, French","points":7969.69,"variation":0.4},"NIKKEI":{"name":"Nikkei 225","location":"Tokyo, Japan","points":42633.29,"variation":0.05}},"available_sources":["BRL"],"taxes":[]},"execution_time":0.0,"from_cache":true}
"{\"by\":\"default\",\"valid_key\":false,\"results\":{\"currencies\":{\"source\":\"BRL\",\"USD\":{\"name\":\"Dollar\",\"buy\":5.4227,\"sell\":5.4222,\"variation\":0.0},\"EUR\":{\"name\":\"Euro\",\"buy\":6.353,\"sell\":6.3573,\"variation\":0.0},\"GBP\":{\"name\":\"Pound Sterling\",\"buy\":7.3346,\"sell\":null,\"variation\":0.0},\"ARS\":{\"name\":\"Argentine Peso\",\"buy\":0.0039,\"sell\":null,\"variation\":0.0},\"CAD\":{\"name\":\"Canadian Dollar\",\"buy\":3.9212,\"sell\":null,\"variation\":0.0},\"AUD\":{\"name\":\"Australian Dollar\",\"buy\":3.5173,\"sell\":null,\"variation\":0.0},\"JPY\":{\"name\":\"Japanese Yen\",\"buy\":0.0368,\"sell\":null,\"variation\":0.0},\"CNY\":{\"name\":\"Renminbi\",\"buy\":0.7564,\"sell\":null,\"variation\":0.0},\"BTC\":{\"name\":\"Bitcoin\",\"buy\":657801.145,\"sell\":657801.145,\"variation\":-2.123}},\"stocks\":{\"IBOVESPA\":{\"name\":\"BM\\u0026F BOVESPA\",\"location\":\"Sao Paulo, Brazil\",\"points\":137968.16,\"variation\":2.57},\"IFIX\":{\"name\":\"Índice de Fundos de Investimentos Imobiliários B3\",\"location\":\"Sao Paulo, Brazil\",\"points\":3430.09,\"variation\":0.13},\"NASDAQ\":{\"name\":\"NASDAQ Stock Market\",\"location\":\"New York City, United States\",\"points\":21496.54,\"variation\":1.88},\"DOWJONES\":{\"name\":\"Dow Jones Industrial Average\",\"location\":\"New York City, United States\",\"points\":45631.74,\"variation\":1.89},\"CAC\":{\"name\":\"CAC 40\",\"location\":\"Paris, French\",\"points\":7969.69,\"variation\":0.4},\"NIKKEI\":{\"name\":\"Nikkei 225\",\"location\":\"Tokyo, Japan\",\"points\":42633.29,\"variation\":0.05}},\"available_sources\":[\"BRL\"],\"taxes\":[]},\"execution_time\":0.0,\"from_cache\":true}
```

9. Criar a model classe para representar as cotações de moedas individuais.

```java
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Currency {
    private String name;
    private Double buy;
    private Double sell;
    private Double variation;
}
```

10. Criar a classe CurrenciesDTO para representar o JSON retornado pela API considerando os nós results, currencies e as moedas USD e EUR.

```java
package br.gov.sp.cps.fatecararas.productms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrenciesDTO {
    @JsonProperty("results")
    private ResultDTO result;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultDTO {
        @JsonProperty("currencies")
        private CurrenciesDataDTO data;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrenciesDataDTO {
        @JsonProperty("USD")
        private CurrencyDTO dollar;
        @JsonProperty("EUR")
        private CurrencyDTO euro;
    }
}

```

11. Atualize o serviço UpdateCurrencyService para que ele converta o JSON retornado pela API para um objeto CurrenciesDTO.

```java
@Service
public class UpdateCurrencyService {

    private final RestClient client;

    public UpdateCurrencyService(RestClient client) {
        this.client = client;
    }

    @Scheduled(fixedRate = 500000)
    public void update() throws JsonProcessingException {
        System.out.println("Updating currency");
        CurrenciesDTO response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(CurrenciesDTO.class);
        
        System.out.println(response);
        System.out.println(new ObjectMapper().writeValueAsString(response));
    }
}
```

12. Execute o projeto e verifique se as cotações de moedas foram atualizadas corretamente.

8. Configure os Beans do Spring Boot para que o projeto seja iniciado corretamente considerando a desserializaçao para a classe CurrenciesDTO.

```shell
Updating currency
CurrenciesDTO(result=ResultDTO(data=CurrenciesDataDTO(dollar=CurrencyDTO(name=Dollar, buy=5.4227, sell=5.4222, variation=0.0), euro=CurrencyDTO(name=Euro, buy=6.353, sell=6.3573, variation=0.0))))
{"results":{"currencies":{"USD":{"name":"Dollar","buy":5.4227,"sell":5.4222,"variation":0.0},"EUR":{"name":"Euro","buy":6.353,"sell":6.3573,"variation":0.0}}}}
```

13. Vamos extrair as cotações de moedas do JSON retornado pela API; atualize a classe UpdateCurrencyService.

```java
@Scheduled(fixedRate = 500000)
    public void update() throws JsonProcessingException {
        System.out.println("Updating currency");
        CurrenciesDTO response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(CurrenciesDTO.class);

        if (response == null) {
            System.out.println("Response is null");
            return;
        }

        var dollar = response.getResult().getData().getDollar();
        var euro = response.getResult().getData().getEuro();

        System.out.println(dollar);
        System.out.println(euro);
    }
```

14. Execute o projeto e verifique se as cotações de moedas foram atualizadas corretamente.

```shell
Updating currency
CurrencyDTO(name=Dollar, buy=5.4227, sell=5.4222, variation=0.0)
CurrencyDTO(name=Euro, buy=6.353, sell=6.3573, variation=0.0)
```

15. Goara que temos nossas cotações de moedas, vamos atualizar as tabelas de moedas. Mas primeiro devemos converter os DTOs em entidades para podermos persisti-las na base de dados. Iremo utilizar o pacote modelmapper para realizar a conversão.

16. Adicione o modelmapper no arquivo build.gradle.

```gradle
implementation 'org.modelmapper:modelmapper:3.1.1'
```

17. Atualize o arquivo AppConfig.java para disponibilizar o modelmapper como um Bean.

```java
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

18. Crie os repositorios de moedas para a persistência.

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface DollarRepository extends JpaRepository<Dollar, Long> {
}

public interface EuroRepository extends JpaRepository<Euro, Long> {
}
```

19. Faça a injeção de dependência do modelmapper no arquivo UpdateCurrencyService.java, assim como os repositorios de moedas.

```java
@Service
public class UpdateCurrencyService {

    private final RestClient client;
    private final DollarRepository dollarRepository;
    private final EuroRepository euroRepository;
    // Declaração do bean ModelMapper
    private final ModelMapper modelMapper;

    public UpdateCurrencyService(RestClient client, DollarRepository dollarRepository, EuroRepository euroRepository, ModelMapper modelMapper) {
        this.client = client;
        this.dollarRepository = dollarRepository;
        this.euroRepository = euroRepository;
        this.modelMapper = modelMapper;
    }
    // Restante do código aqui...
}
```

20. Atualize o arquivo UpdateCurrencyService.java para que ele converta os DTOs em entidades e persista-as na base de dados.

```java
@Scheduled(fixedRate = 500000)
    public void update() throws JsonProcessingException {
        System.out.println("Updating currency");
        CurrenciesDTO response = client.get()
                .uri("https://api.hgbrasil.com/finance")
                .retrieve().body(CurrenciesDTO.class);

        if (response == null) {
            System.out.println("Response is null");
            return;
        }

        var dollar = response.getResult().getData().getDollar();
        var euro = response.getResult().getData().getEuro();

        // Conversão de DTOs em Entities
        Dollar dollarEntity = modelMapper.map(dollar, Dollar.class);
        Euro euroEntity = modelMapper.map(euro, Euro.class);

        dollarRepository.save(dollarEntity);
        euroRepository.save(euroEntity);
    }
```

21. Como estamos utilizando herança, precisamos ajustar a classe Currency para que ela gere as sequences de forma correta.

```java
@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Currency {
    @Id
    // Aqui usamos a sequence que sera ajustata na migration V2__CreateTableCurrencies.sql
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_seq")
    @SequenceGenerator(name = "currency_seq", sequenceName = "currency_seq", allocationSize = 1)
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

22. Crie a sequence no arquivo V2__CreateTableCurrencies.sql.

```sql
-- Criação de sequence
CREATE SEQUENCE currency_seq START WITH 1 INCREMENT BY 1;

-- Criação de tabelas
CREATE TABLE Dollar (
  -- Configuração de sequence
  id BIGINT DEFAULT NEXT VALUE FOR currency_seq PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  buy DECIMAL(19, 4) NOT NULL,
  sell DECIMAL(19, 4) NOT NULL,
  variation DECIMAL(19, 4) NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Euro (
  -- Configuração de sequence
  id BIGINT DEFAULT NEXT VALUE FOR currency_seq PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  buy DECIMAL(19, 4) NOT NULL,
  sell DECIMAL(19, 4) NOT NULL,
  variation DECIMAL(19, 4) NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

23. Execute o projeto e verifique se as cotações de moedas foram atualizadas corretamente fazendo select no banco de dados.







