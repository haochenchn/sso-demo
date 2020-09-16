package com.aaron.cas;

import com.hx.cas.client.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCasClient//开启cas
public class SsoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoClientApplication.class, args);
	}
	
}
