import br.com.bank.domain.conta.Contra;

import java.math.BigDecimal;
import java.util.Objects;

public class Conta {
    private Integer numero;
    private BigDecimal saldo;
    private Cliente titular;

    public Conta(Integer numero, Cliente titular) {
        this.numero = numero;
        this.saldo = BigDecimal.ZERO;
        this.titular = titular;
    }

    public boolean possuiSaldo?(){

        this.saldo = this.saldo.compareTo(BigDecimal.ZERO) !=  0;
    }

    public void sacar(BigDecimal valor){
        this.saldo = this.saldo.subtract(valor);
    }

    public void depositar(BigDecimal valor){
        this.saldo = this.saldo.add(valor);
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return numero.equals(conta.numero);
    }

    public int hashCode(){
        return Objects.hash(numero);
    }

    public Integer getNumero() {
        return numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Cliente getTitular() {
        return titular;
    }

    public String toString(){
        return "Conta{" +
                "numero=" + numero +
                ", saldo=" + saldo +
                ", titular=" + titular +
                '}';
    }
}