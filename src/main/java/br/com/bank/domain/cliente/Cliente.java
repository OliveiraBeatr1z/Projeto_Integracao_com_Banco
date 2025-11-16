package br.com.bank.domain.cliente;

import java.util.Objects;

public class Cliente {
    private String name;
    private String cpf;
    private String email;

    public Cliente(DadosCadastroCliente dados){
        this.name = dados.name();
        this.cpf = dados.cpf();
        this.email = dados.email();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode(){
        return Objects.hash(cpf);
    }

    public String getName(){
        return name;
    }

    public String getCpf(){
        return cpf;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString(){
        return "Cliente{name='" + name + "', cpf='" + cpf + "', email='" + email + "'}";
    }
}

