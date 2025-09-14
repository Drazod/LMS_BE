package com.curcus.lms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication  // (exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.curcus.lms")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(basePackages = "com.curcus.lms.repository")
@CrossOrigin(origins = "*")

public class CurcusApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurcusApplication.class, args);
	}
	@Bean
    public CommandLineRunner testConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("SELECT 1");
                System.out.println("Connection successful!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}
