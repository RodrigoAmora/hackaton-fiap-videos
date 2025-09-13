package br.com.fiap.fiapvideos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FiapVideosApplication {

    public static void main(String[] args) {
        SpringApplication.run(FiapVideosApplication.class, args);
    }

}
