package com.imagine.card.card_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CardApplicationApp {

	public static void main(String[] args) {
		SpringApplication.run(CardApplicationApp.class, args);
	}

}
