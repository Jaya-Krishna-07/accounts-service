package com.app.accounts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(title = "Accounts microservice Rest API Documentation",
                 description = "Bank Accounts microservice Rest API Documentation",
                 version = "v1",
                 contact = @Contact(name = "Jaya Krishna", email = "jayamannem@gmail.com", url = "hello.comm")))
public class AccountsApplication {
    public static void main(String[] args) { SpringApplication.run(AccountsApplication.class, args); }
}
