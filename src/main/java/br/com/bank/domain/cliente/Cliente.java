package br.com.bank.domain.cliente;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "cpf")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String email;

    public Cliente(DadosCadastroCliente dados){
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.email = dados.email();
    }

    public Cliente() {
        // Construtor padrão exigido pelo JPA
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }
    public String getCpf() {
        return cpf;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString(){
        return "Cliente{nome='" + nome + "', cpf='" + cpf + "', email='" + email + "'}";
    }
}
