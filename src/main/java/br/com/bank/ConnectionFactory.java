package br.com.bank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection conectarDB(){
        try{
            return createDataSource().getConnection();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();// string de conexao
        config.setJdbcUrl("jdbc:mysql://localhost:3306/bytebank");
        config.setUsername("root");
        config.setPassword("8250");
        config.setMaximumPoolSize(10); // quantas conexões podem ficar abertas ao mesmo tempo para serem utilizadas

        return new HikariDataSource(config);
    }
}
