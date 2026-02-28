package com.seohalabs.moduerp.vacation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VacationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacationApplication.class, args);
    }
}
