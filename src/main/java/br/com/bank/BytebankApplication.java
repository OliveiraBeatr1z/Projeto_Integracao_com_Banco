package br.com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BytebankApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(BytebankApplication.class, args);
        Environment env = ctx.getEnvironment();
        String port = env.getProperty("local.server.port");
        System.out.println("=== ByteBank API está rodando! ===");
        System.out.println("Acesse: http://localhost:" + port + "/api/contas");
        System.out.println("=====================================");
    }
}
