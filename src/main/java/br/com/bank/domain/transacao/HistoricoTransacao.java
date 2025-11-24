package br.com.bank.domain.transacao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "historico_transacao")
public class HistoricoTransacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", nullable = false)
    private Integer numeroConta;

    @Column(name = "tipo_operacao", nullable = false)
    private String tipoOperacao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "saldo_anterior", precision = 19, scale = 2)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_novo", precision = 19, scale = 2)
    private BigDecimal saldoNovo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 500)
    private String descricao;

    public HistoricoTransacao() {}

    public HistoricoTransacao(Integer numeroConta, String tipoOperacao, BigDecimal valor, BigDecimal saldoAnterior, BigDecimal saldoNovo, LocalDateTime dataHora, String descricao) {
        this.numeroConta = numeroConta;
        this.tipoOperacao = tipoOperacao;
        this.valor = valor;
        this.saldoAnterior = saldoAnterior;
        this.saldoNovo = saldoNovo;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public Integer getNumeroConta() { return numeroConta; }
    public void setNumeroConta(Integer numeroConta) { this.numeroConta = numeroConta; }
    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public BigDecimal getSaldoNovo() { return saldoNovo; }
    public void setSaldoNovo(BigDecimal saldoNovo) { this.saldoNovo = saldoNovo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoricoTransacao)) return false;
        HistoricoTransacao that = (HistoricoTransacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

