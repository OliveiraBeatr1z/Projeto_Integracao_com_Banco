package br.com.bank.controller;

import br.com.bank.domain.transacao.HistoricoTransacao;
import br.com.bank.domain.transacao.HistoricoTransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/historico")
public class HistoricoTransacaoController {

    @Autowired
    private HistoricoTransacaoRepository historicoRepository;
    
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/conta/{numero}")
    public ResponseEntity<List<HistoricoTransacao>> consultarHistoricoConta(
            @PathVariable Integer numero) {
        List<HistoricoTransacao> historico = historicoRepository
                .findByNumeroContaOrderByDataHoraDesc(numero);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/conta/{numero}/periodo")
    public ResponseEntity<List<HistoricoTransacao>> consultarHistoricoPorPeriodo(
            @PathVariable Integer numero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);
        
        List<HistoricoTransacao> historico = historicoRepository
                .findByNumeroContaAndDataHoraBetweenOrderByDataHoraDesc(numero, inicio, fim);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<HistoricoTransacao>> consultarPorTipo(
            @PathVariable String tipo) {
        List<HistoricoTransacao> historico = historicoRepository
                .findByTipoOperacao(tipo.toUpperCase());
        return ResponseEntity.ok(historico);
    }

    // ========== ENDPOINTS QUE USAM PROCEDURES ==========

    @SuppressWarnings("unchecked")
    @GetMapping("/extrato/{numero}")
    public ResponseEntity<Map<String, Object>> consultarExtrato(
            @PathVariable Integer numero,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_extrato_conta");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, java.sql.Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, java.sql.Date.class, ParameterMode.IN);
        
        query.setParameter(1, numero);
        query.setParameter(2, dataInicio != null ? java.sql.Date.valueOf(dataInicio) : null);
        query.setParameter(3, dataFim != null ? java.sql.Date.valueOf(dataFim) : null);
        
        List<Object[]> resultList = query.getResultList();
        
        List<Map<String, Object>> extrato = new ArrayList<>();
        for (Object[] row : resultList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", row[0]);
            item.put("numeroConta", row[1]);
            item.put("tipoOperacao", row[2]);
            item.put("valor", row[3]);
            item.put("saldoAnterior", row[4]);
            item.put("saldoNovo", row[5]);
            item.put("dataHora", row[6]);
            item.put("descricao", row[7]);
            extrato.add(item);
        }
        
        return ResponseEntity.ok(Map.of(
            "numeroConta", numero,
            "periodo", Map.of(
                "inicio", dataInicio != null ? dataInicio : "início",
                "fim", dataFim != null ? dataFim : "hoje"
            ),
            "transacoes", extrato,
            "totalTransacoes", extrato.size()
        ));
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/relatorio/saldo-total")
    public ResponseEntity<Map<String, Object>> consultarSaldoTotal() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_saldo_total_banco");
        
        List<Object[]> resultList = query.getResultList();
        
        if (!resultList.isEmpty()) {
            Object[] row = resultList.get(0);
            return ResponseEntity.ok(Map.of(
                "totalContas", row[0],
                "saldoTotal", row[1],
                "saldoMedio", row[2],
                "maiorSaldo", row[3],
                "menorSaldo", row[4]
            ));
        }
        
        return ResponseEntity.ok(Map.of("mensagem", "Nenhuma conta encontrada"));
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/relatorio/contas-saldo-baixo")
    public ResponseEntity<List<Map<String, Object>>> consultarContasSaldoBaixo(
            @RequestParam(defaultValue = "100.00") BigDecimal limiteSaldo) {
        
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_contas_saldo_baixo");
        query.registerStoredProcedureParameter(1, BigDecimal.class, ParameterMode.IN);
        query.setParameter(1, limiteSaldo);
        
        List<Object[]> resultList = query.getResultList();
        List<Map<String, Object>> contas = new ArrayList<>();
        
        for (Object[] row : resultList) {
            contas.add(Map.of(
                "numero", row[0],
                "saldo", row[1],
                "nome", row[2],
                "cpf", row[3],
                "email", row[4]
            ));
        }
        
        return ResponseEntity.ok(contas);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/admin/aplicar-taxa")
    public ResponseEntity<Map<String, Object>> aplicarTaxaManutencao(
            @RequestBody Map<String, BigDecimal> body) {
        
        BigDecimal valorTaxa = body.get("valorTaxa");
        
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_aplicar_taxa_manutencao");
        query.registerStoredProcedureParameter(1, BigDecimal.class, ParameterMode.IN);
        query.setParameter(1, valorTaxa);
        
        List<Object[]> resultList = query.getResultList();
        
        if (!resultList.isEmpty()) {
            Object[] row = resultList.get(0);
            return ResponseEntity.ok(Map.of(
                "contasAfetadas", row[0],
                "valorTaxa", row[1],
                "totalArrecadado", row[2],
                "mensagem", "Taxa aplicada com sucesso"
            ));
        }
        
        return ResponseEntity.ok(Map.of("mensagem", "Nenhuma conta foi afetada"));
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/relatorio/movimentacoes")
    public ResponseEntity<List<Map<String, Object>>> relatorioMovimentacoes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_relatorio_movimentacoes");
        query.registerStoredProcedureParameter(1, java.sql.Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, java.sql.Date.class, ParameterMode.IN);
        query.setParameter(1, java.sql.Date.valueOf(dataInicio));
        query.setParameter(2, java.sql.Date.valueOf(dataFim));
        
        List<Object[]> resultList = query.getResultList();
        List<Map<String, Object>> relatorio = new ArrayList<>();
        
        for (Object[] row : resultList) {
            relatorio.add(Map.of(
                "tipoOperacao", row[0],
                "quantidadeOperacoes", row[1],
                "valorTotal", row[2],
                "valorMedio", row[3],
                "maiorValor", row[4],
                "menorValor", row[5]
            ));
        }
        
        return ResponseEntity.ok(relatorio);
    }
}

