package br.com.bank.domain.transacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoTransacaoRepository extends JpaRepository<HistoricoTransacao, Long> {
    List<HistoricoTransacao> findByNumeroContaOrderByDataHoraDesc(Integer numeroConta);
    List<HistoricoTransacao> findByNumeroContaAndDataHoraBetweenOrderByDataHoraDesc(Integer numeroConta, LocalDateTime inicio, LocalDateTime fim);
    List<HistoricoTransacao> findByTipoOperacao(String tipoOperacao);
}

