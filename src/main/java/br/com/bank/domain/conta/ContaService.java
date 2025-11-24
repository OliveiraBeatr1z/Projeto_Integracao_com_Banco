package br.com.bank.domain.conta;

import br.com.bank.domain.RegraDeNegocioException;
import br.com.bank.domain.cliente.Cliente;
import br.com.bank.domain.cliente.ClienteRepository;
import br.com.bank.domain.transacao.HistoricoTransacao;
import br.com.bank.domain.transacao.HistoricoTransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private HistoricoTransacaoRepository historicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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

        String cpf = dadosDaConta.dadosCliente().cpf();
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseGet(() -> new Cliente(dadosDaConta.dadosCliente()));

        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);

        contaRepository.save(conta);

        registrarHistorico(conta.getNumero(), "ABERTURA", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Abertura da conta e saldo inicial.");
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

        BigDecimal saldoAnterior = conta.getSaldo();
        BigDecimal saldoNovo = saldoAnterior.subtract(valor);

        conta.setSaldo(saldoNovo);
        contaRepository.save(conta);

        registrarHistorico(conta.getNumero(), "SAQUE", valor, saldoAnterior, saldoNovo, "Saque em dinheiro.");
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

        BigDecimal saldoAnterior = conta.getSaldo();
        BigDecimal saldoNovo = saldoAnterior.add(valor);

        conta.setSaldo(saldoNovo);
        contaRepository.save(conta);

        registrarHistorico(conta.getNumero(), "DEPÓSITO", valor, saldoAnterior, saldoNovo, "Depósito em dinheiro.");
    }

    @Transactional
    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        BigDecimal saldoAtual = conta.getSaldo();
        contaRepository.delete(conta);

        registrarHistorico(conta.getNumero(), "ENCERRAMENTO", BigDecimal.ZERO, saldoAtual, saldoAtual, "Conta encerrada (Exclusão física).");
    }

    public Conta buscarContaPorNumero(Integer numero) {
        return contaRepository.findByNumero(numero)
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }

    @Transactional
    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {
        var contaOrigem = buscarContaPorNumero(numeroContaOrigem);
        var contaDestino = buscarContaPorNumero(numeroContaDestino);

        if (!contaOrigem.getEstaAtiva() || !contaDestino.getEstaAtiva()) {
            throw new RegraDeNegocioException("Ambas as contas devem estar ativas para a transferência.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor da transferência deve ser maior que zero!");
        }
        if (valor.compareTo(contaOrigem.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente na conta de origem!");
        }
        if (numeroContaOrigem.equals(numeroContaDestino)) {
            throw new RegraDeNegocioException("Conta de origem e destino não podem ser as mesmas.");
        }

        BigDecimal saldoAnteriorOrigem = contaOrigem.getSaldo();
        BigDecimal saldoNovoOrigem = saldoAnteriorOrigem.subtract(valor);
        contaOrigem.setSaldo(saldoNovoOrigem);

        BigDecimal saldoAnteriorDestino = contaDestino.getSaldo();
        BigDecimal saldoNovoDestino = saldoAnteriorDestino.add(valor);
        contaDestino.setSaldo(saldoNovoDestino);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        registrarHistorico(numeroContaOrigem, "TRANSFERÊNCIA ENVIADA", valor, saldoAnteriorOrigem, saldoNovoOrigem, "Transferência para conta " + numeroContaDestino);
        registrarHistorico(numeroContaDestino, "TRANSFERÊNCIA RECEBIDA", valor, saldoAnteriorDestino, saldoNovoDestino, "Transferência da conta " + numeroContaOrigem);
    }

    @Transactional
    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        conta.setEstaAtiva(false);
        contaRepository.save(conta);

        BigDecimal saldoAtual = conta.getSaldo();
        registrarHistorico(conta.getNumero(), "ENCERRAMENTO LÓGICO", BigDecimal.ZERO, saldoAtual, saldoAtual, "Conta desativada (saldo zero).");
    }

    private void registrarHistorico(Integer numeroConta, String tipo, BigDecimal valor, BigDecimal saldoAnterior, BigDecimal saldoNovo, String descricao) {
        HistoricoTransacao historico = new HistoricoTransacao(
                numeroConta,
                tipo,
                valor,
                saldoAnterior,
                saldoNovo,
                LocalDateTime.now(),
                descricao
        );
        historicoRepository.save(historico);
    }

    public List<HistoricoTransacao> consultarExtrato(Integer numeroDaConta, LocalDateTime dataInicio, LocalDateTime dataFim) {
        buscarContaPorNumero(numeroDaConta);

        if (dataInicio == null || dataFim == null) {
            return historicoRepository.findByNumeroContaOrderByDataHoraDesc(numeroDaConta);
        }

        LocalDateTime fimDoDia = dataFim.withHour(23).withMinute(59).withSecond(59);

        return historicoRepository.findByNumeroContaAndDataHoraBetweenOrderByDataHoraDesc(
                numeroDaConta,
                dataInicio,
                fimDoDia
        );
    }

    public BigDecimal consultarSaldoTotalBanco() {
        Set<Conta> contasAtivas = listarContasAbertas();
        return contasAtivas.stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Set<Conta> listarTodasContas() {
        return new HashSet<>(contaRepository.findAll());
    }

    public Set<Conta> listarContasComSaldoBaixo(BigDecimal limite) {
        if (limite.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraDeNegocioException("O limite de saldo não pode ser negativo.");
        }
        return listarContasAbertas().stream()
                .filter(conta -> conta.getSaldo().compareTo(limite) < 0)
                .collect(Collectors.toSet());
    }

    public Map<String, Long> consultarRelatorioMovimentacoes(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new RegraDeNegocioException("É necessário informar um período (data inicial e data final) para o relatório.");
        }

        LocalDateTime fimDoDia = dataFim.withHour(23).withMinute(59).withSecond(59);
        
        List<HistoricoTransacao> todasTransacoes = historicoRepository.findAll();

        return todasTransacoes.stream()
                .filter(t -> t.getDataHora() != null && !t.getDataHora().isBefore(dataInicio) && !t.getDataHora().isAfter(fimDoDia))
                .collect(Collectors.groupingBy(
                        HistoricoTransacao::getTipoOperacao,
                        Collectors.counting()
                ));
    }
}
