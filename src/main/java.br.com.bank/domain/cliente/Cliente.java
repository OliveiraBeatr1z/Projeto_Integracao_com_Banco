package br.com.bank.domain.cliente.Cliente;

import java.util.Objects;

public class Client{
    private String name;
    private String cpf;
    private String email;

    public Cliente(DadosCadastroCliente dados){
        this.name = dados.name();
        this.cpf = dados.cpf();
        this.email = dados.email();
    }

    @override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return cpf.equals(cliente.cpf);
    }

    @override
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
}









