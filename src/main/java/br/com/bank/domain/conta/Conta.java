package br.com.bank.domain.conta;

import br.com.bank.domain.cliente.Cliente;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "conta")
@NamedStoredProcedureQuery(
    name = "Conta.desativarConta",
    procedureName = "desativar_conta",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_numero", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_status", type = Boolean.class)
    }
)
public class Conta {

    @Id
    private Integer numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente titular;

    @Column(name = "esta_ativa", nullable = false)
    private boolean estaAtiva = true;

    public Conta() {
    }

    public Conta(Integer numero, BigDecimal saldo, Cliente titular, boolean estaAtiva) {
        this.numero = numero;
        this.saldo = saldo == null ? BigDecimal.ZERO : saldo;
        this.titular = titular;
        this.estaAtiva = estaAtiva;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo == null ? BigDecimal.ZERO : saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo == null ? BigDecimal.ZERO : saldo;
    }

    public Cliente getTitular() {
        return titular;
    }

    public void setTitular(Cliente titular) {
        this.titular = titular;
    }

    public boolean getEstaAtiva() {
        return estaAtiva;
    }

    public void setEstaAtiva(boolean estaAtiva) {
        this.estaAtiva = estaAtiva;
    }

    public boolean possuiSaldo(){
        return getSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString(){
        return "Conta{" +
                "numero=" + numero +
                ", saldo=" + saldo +
                ", titular=" + titular +
                ", estaAtiva=" + estaAtiva +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conta)) return false;
        Conta conta = (Conta) o;
        return Objects.equals(numero, conta.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }
}
