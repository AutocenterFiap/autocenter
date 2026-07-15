package br.com.autocenterfiap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
// Força o Spring a achar seus JpaRepositories no novo caminho de infraestrutura
@EnableJpaRepositories(basePackages = "br.com.autocenterfiap.*")
// Força o Hibernate/JPA a achar as classes com @Entity
@EntityScan(basePackages = "br.com.autocenterfiap.*")
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

}
