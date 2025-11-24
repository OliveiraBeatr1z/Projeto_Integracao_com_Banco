package br.com.bank.controller;

import br.com.bank.domain.conta.Conta;
import br.com.bank.domain.conta.ContaService;
import br.com.bank.domain.conta.DadosAberturaConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping
    public ResponseEntity<Set<Conta>> listarContasAbertas() {
        return ResponseEntity.ok(contaService.listarContasAbertas());
    }

    @PostMapping
    public ResponseEntity<String> abrirConta(@RequestBody DadosAberturaConta dados) {
        contaService.abrir(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body("Conta aberta com sucesso!");
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<Map<String, BigDecimal>> consultarSaldo(@PathVariable Integer numero) {
        BigDecimal saldo = contaService.consultarSaldo(numero);
        return ResponseEntity.ok(Map.of("saldo", saldo));
    }

    @PostMapping("/{numero}/saque")
    public ResponseEntity<String> realizarSaque(
            @PathVariable Integer numero, 
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal valor = body.get("valor");
        contaService.realizarSaque(numero, valor);
        return ResponseEntity.ok("Saque realizado com sucesso!");
    }

    @PostMapping("/{numero}/deposito")
    public ResponseEntity<String> realizarDeposito(
            @PathVariable Integer numero, 
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal valor = body.get("valor");
        contaService.realizarDeposito(numero, valor);
        return ResponseEntity.ok("Depósito realizado com sucesso!");
    }

    @PostMapping("/transferencia")
    public ResponseEntity<String> realizarTransferencia(@RequestBody Map<String, Object> body) {
        Integer numeroOrigem = (Integer) body.get("numeroOrigem");
        Integer numeroDestino = (Integer) body.get("numeroDestino");
        BigDecimal valor = new BigDecimal(body.get("valor").toString());
        
        contaService.realizarTransferencia(numeroOrigem, numeroDestino, valor);
        return ResponseEntity.ok("Transferência realizada com sucesso!");
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<String> encerrarConta(@PathVariable Integer numero) {
        contaService.encerrarLogico(numero);
        return ResponseEntity.ok("Conta encerrada com sucesso!");
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Conta> buscarConta(@PathVariable Integer numero) {
        Conta conta = contaService.buscarContaPorNumero(numero);
        return ResponseEntity.ok(conta);
    }
}

