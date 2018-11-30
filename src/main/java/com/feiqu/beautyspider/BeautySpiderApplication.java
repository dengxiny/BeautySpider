package com.feiqu.beautyspider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.feiqu"})
//@EnableTransactionManagement
public class BeautySpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautySpiderApplication.class, args);
	}
}
