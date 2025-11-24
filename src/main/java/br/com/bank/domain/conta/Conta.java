package br.com.bank.domain.conta;

import br.com.bank.domain.cliente.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "conta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "numero")
public class Conta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Integer numero;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente titular;
    
    @Column(name = "esta_ativa", nullable = false)
    private Boolean estaAtiva = true;

    public Conta(Integer numero, BigDecimal saldo, Cliente titular, Boolean estaAtiva) {
        this.numero = numero;
        this.saldo = saldo;
        this.titular = titular;
        this.estaAtiva = estaAtiva;
    }

    public boolean possuiSaldo(){
        return this.saldo.compareTo(BigDecimal.ZERO) > 0;
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
}

