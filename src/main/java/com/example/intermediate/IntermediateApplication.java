package com.example.intermediate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IntermediateApplication {

  public static void main(String[] args) {
    SpringApplication.run(IntermediateApplication.class, args);
  }

}
