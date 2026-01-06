package com.notificationservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class NotificationServiceApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	private static void loadEnvVariables() {
		try {
			io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
					.directory("../")
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});
			System.out.println("Loaded .env file successfully.");
		} catch (Exception e) {
			System.out.println(".env file not found, using system envs.");
		}
	}

}
