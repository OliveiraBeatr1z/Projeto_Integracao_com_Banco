package br.com.bank.domain.conta;

import br.com.bank.domain.RegraDeNegocioException;
import br.com.bank.domain.cliente.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Set<Conta> listarContasAbertas() {
        return new HashSet<>(contaRepository.findByEstaAtivaTrue());
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    @Transactional
    public void abrir(DadosAberturaConta dadosDaConta) {
        if (contaRepository.existsByNumero(dadosDaConta.numero())) {
            throw new RegraDeNegocioException("Já existe uma conta com esse número!");
        }
        
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);
        
        contaRepository.save(conta);
    }

    @Transactional
    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Conta não está ativa");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor do saque deve ser maior que zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Conta não está ativa");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor do depósito deve ser maior que zero!");
        }
        
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        contaRepository.delete(conta);
    }

    public Conta buscarContaPorNumero(Integer numero) {
        return contaRepository.findByNumero(numero)
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }

    @Transactional
    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroContaOrigem, valor);
        this.realizarDeposito(numeroContaDestino, valor);
    }

    @Transactional
    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        conta.setEstaAtiva(false);
        contaRepository.save(conta);
    }
}

