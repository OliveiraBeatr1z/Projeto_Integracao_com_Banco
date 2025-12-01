package br.com.bank.controller;

import br.com.bank.domain.RegraDeNegocioException;
import br.com.bank.domain.conta.Conta;
import br.com.bank.domain.conta.ContaService;
import br.com.bank.domain.conta.DadosAberturaConta;
import br.com.bank.domain.transacao.HistoricoTransacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService service;

    @GetMapping
    public ResponseEntity<Set<Conta>> listar() {
        return ResponseEntity.ok(service.listarContasAbertas());
    }

    @PostMapping
    public ResponseEntity<String> abrir(@RequestBody DadosAberturaConta dados) {
        try {
            service.abrir(dados);
            return ResponseEntity.ok("Conta aberta com sucesso!");
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable Integer numero) {
        return ResponseEntity.ok(service.consultarSaldo(numero));
    }

    @PostMapping("/{numero}/deposito")
    public ResponseEntity<String> depositar(@PathVariable Integer numero, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal valor = request.get("valor");
            service.realizarDeposito(numero, valor);
            return ResponseEntity.ok("Depósito realizado com sucesso!");
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{numero}/saque")
    public ResponseEntity<String> sacar(@PathVariable Integer numero, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal valor = request.get("valor");
            service.realizarSaque(numero, valor);
            return ResponseEntity.ok("Saque realizado com sucesso!");
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/transferencia")
    public ResponseEntity<String> transferir(@RequestBody Map<String, Object> request) {
        try {
            Integer numeroOrigem = (Integer) request.get("numeroContaOrigem");
            Integer numeroDestino = (Integer) request.get("numeroContaDestino");
            BigDecimal valor = new BigDecimal(request.get("valor").toString());
            service.realizarTransferencia(numeroOrigem, numeroDestino, valor);
            return ResponseEntity.ok("Transferência realizada com sucesso!");
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{numero}/logico")
    public ResponseEntity<String> encerrarLogico(@PathVariable Integer numero) {
        try {
            service.encerrarLogico(numero);
            return ResponseEntity.ok("Conta encerrada (logicamente) com sucesso!");
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{numero}/extrato")
    public ResponseEntity<?> consultarExtrato(
            @PathVariable Integer numero,
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        try {
            LocalDateTime dataInicio = null;
            LocalDateTime dataFim = null;

            if (inicio != null && !inicio.isEmpty()) {
                dataInicio = java.time.LocalDate.parse(inicio).atStartOfDay();
            }
            if (fim != null && !fim.isEmpty()) {
                dataFim = java.time.LocalDate.parse(fim).atStartOfDay();
            }

            List<HistoricoTransacao> extrato = service.consultarExtrato(numero, dataInicio, dataFim);
            return ResponseEntity.ok(extrato);

        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de data inválido. Use o formato yyyy-MM-dd.");
        }
    }

    @GetMapping("/relatorios/geral")
    public ResponseEntity<Map<String, Object>> consultarRelatorioGeral() {
        Set<Conta> contas = service.listarTodasContas();

        if (contas.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("mensagem", "Nenhuma conta encontrada."));
        }

        long totalContas = contas.stream().filter(Conta::getEstaAtiva).count();
        List<BigDecimal> saldos = contas.stream()
                .filter(Conta::getEstaAtiva)
                .map(Conta::getSaldo)
                .collect(Collectors.toList());

        BigDecimal saldoTotal = service.consultarSaldoTotalBanco();
        BigDecimal saldoMedio = saldos.isEmpty() ? BigDecimal.ZERO : saldoTotal.divide(BigDecimal.valueOf(saldos.size()), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal maiorSaldo = saldos.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal menorSaldo = saldos.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        Map<String, Object> relatorio = Map.of(
                "totalContasAtivas", totalContas,
                "saldoTotal", saldoTotal,
                "saldoMedio", saldoMedio,
                "maiorSaldo", maiorSaldo,
                "menorSaldo", menorSaldo
        );

        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/relatorios/saldo-baixo")
    public ResponseEntity<?> listarContasBaixoSaldo(@RequestParam(required = true) BigDecimal limite) {
        try {
            Set<Conta> contas = service.listarContasComSaldoBaixo(limite);
            return ResponseEntity.ok(contas);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/relatorios/movimentacoes")
    public ResponseEntity<?> consultarRelatorioMovimentacoes(
            @RequestParam(required = true) String inicio,
            @RequestParam(required = true) String fim) {
        try {
            LocalDateTime dataInicio = java.time.LocalDate.parse(inicio).atStartOfDay();
            LocalDateTime dataFim = java.time.LocalDate.parse(fim).atStartOfDay();

            Map<String, Long> relatorio = service.consultarRelatorioMovimentacoes(dataInicio, dataFim);
            return ResponseEntity.ok(relatorio);
        } catch (RegraDeNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de data inválido. Use o formato yyyy-MM-dd.");
        }
    }

    @PutMapping("/{numero}/desativar")
    public ResponseEntity<?> desativarConta(@PathVariable Integer numero) {
        boolean desativada = service.desativarContaViaProcedure(numero);
        if (desativada) {
            return ResponseEntity.ok("Conta desativada com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Erro ao desativar conta.");
        }
    }
}
