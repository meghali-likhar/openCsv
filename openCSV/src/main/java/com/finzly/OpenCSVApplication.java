package com.finzly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenCSVApplication {

	public static String env;

	public static void main(String[] args) {
		SpringApplication.run(OpenCSVApplication.class, args);
		env = args[0];
	}

}
