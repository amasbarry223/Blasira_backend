package com.example.Blasira_Backend;

import com.example.Blasira_Backend.config.properties.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Point d'entrée Spring Boot de l'application Blasira.
 *
 * Responsabilités clefs:
 * - Bootstrapping du contexte Spring
 * - Activation du binding de configuration pour le stockage (StorageProperties)
 * - Scan des composants dans le package racine de l'application
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@ComponentScan(basePackages = "com.example.Blasira_Backend")
public class BlasiraBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlasiraBackendApplication.class, args);
	}

}
