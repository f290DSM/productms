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

8. Baixe o arquivo `` em [gist](https://gist.github.com/BZR4/ee307cdae9219244929726080d98669c) e coloque-o no diretório `src/main/resources/db/migration`.

9. Execute o projeto, acesse o h2-console e verifique se as tabelas foram criadas corretamente.

10. Acesse o H2 e faça uma consulta visualizar os produtos criados.




