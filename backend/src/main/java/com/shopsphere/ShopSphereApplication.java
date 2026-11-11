package com.shopsphere;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class ShopSphereApplication {
 public static void main(String[] args) { SpringApplication.run(ShopSphereApplication.class, args); }
}
