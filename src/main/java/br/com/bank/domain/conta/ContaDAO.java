package br.com.bank.domain.conta;

import br.com.bank.domain.cliente.Cliente;
import br.com.bank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {
    private  Connection conn;

    // abrir a conexão com o banco de dados
    ContaDAO(Connection connection){
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO,  cliente);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, conta.getNumero());
            ps.setBigDecimal(2, BigDecimal.ZERO);
            ps.setString(3, dadosDaConta.dadosCliente().nome());
            ps.setString(4, dadosDaConta.dadosCliente().cpf());
            ps.setString(5, dadosDaConta.dadosCliente().email());

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar(){
        PreparedStatement ps;
        ResultSet rs;
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta";

        try {
            // faz a conexao e manda a query para o banco de dados
            ps = conn.prepareStatement(sql);

            // resultado do banco de dados
            rs = ps.executeQuery();

            // percorrer o rs para montar o objeto de contas, salvar e mostrar no set.
            while(rs.next()){
                Integer numero = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);

                contas.add(new Conta(numero, saldo, cliente));
            }
            ps.close();
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta listagemPorNumero(Integer numeroDaConta){

        String sql = "SELECT * FROM conta WHERE numero = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroDaConta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer numeroRecuperado = rs.getInt("numero");
                    BigDecimal saldo = rs.getBigDecimal("saldo");
                    String nome = rs.getString("cliente_nome");
                    String cpf = rs.getString("cliente_cpf");
                    String email = rs.getString("cliente_email");

                    DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                    Cliente cliente = new Cliente(dados);

                    return new Conta(numeroRecuperado, saldo, cliente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void alterar(Integer numero, BigDecimal valor ){
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try{
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);
            ps.execute();

            ps.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
