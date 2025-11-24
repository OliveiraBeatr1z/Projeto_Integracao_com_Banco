package br.com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BytebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BytebankApplication.class, args);
        System.out.println("\n=== ByteBank API está rodando! ===");
        System.out.println("Acesse: http://localhost:8080/api/contas");
        System.out.println("=====================================\n");
    }
}

