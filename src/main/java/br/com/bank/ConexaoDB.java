package br.com.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    // metodo main utilizado pra fazer a conexao com o banco
    public static void main(String... x) {

        /* ir no banco de dados buscar a conexao:
        *   url: string de conexão - identifica qual o gerenciador, o tipo do banco, tipo da conexão
        *   OBS: abrimos a conexao, faz a operação e depois fechamos a conexão novamente. */
        try{
            Connection conection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bytebank?user=root&password=8250");
            System.out.println("Conectado com sucesso!");

            conection.close();
        } catch (SQLException e){
            System.out.println( e);
        }
    }
}
