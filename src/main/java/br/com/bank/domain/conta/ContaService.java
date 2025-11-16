package br.com.bank.domain.conta;

import br.com.bank.ConnectionFactory;
import br.com.bank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public class ContaService{

   private ConnectionFactory connection;

   public ContaService(){
        this.connection = new ConnectionFactory();
   }

    private Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas(){
        Connection conn = connection.conectarDB();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo (Integer numeroDaConta){
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir (DadosAberturaConta dadosDaConta){
        Connection conn = connection.conectarDB();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor){
        var conta = buscarContaPorNumero(numeroDaConta);
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor do saque deve ser maior que zero!");
        }

        if(valor.compareTo(conta.getSaldo()) > 0){
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        conta.sacar(valor);

    }

    public void realizarDeposito (Integer numeroDaConta, BigDecimal valor){
        var conta = buscarContaPorNumero(numeroDaConta);
        if(valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new RegraDeNegocioException("O valor do depósito deve ser maior que zero!");
        }
        conta.depositar(valor);
    }

    public void encerrar(Integer numeroDaConta){
        var conta = buscarContaPorNumero(numeroDaConta);
        if(conta.possuiSaldo()){
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }
        contas.remove(conta);
    }
    public Conta buscarContaPorNumero (Integer numeroDaConta){
        return contas
                .stream()
                .filter(c -> c.getNumero().equals(numeroDaConta))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }

    public Conta listagemPorNumero(Integer numeroDaConta){
        Connection conn = connection.conectarDB();
        return new ContaDAO(conn).listagemPorNumero(numeroDaConta);
    }
}

