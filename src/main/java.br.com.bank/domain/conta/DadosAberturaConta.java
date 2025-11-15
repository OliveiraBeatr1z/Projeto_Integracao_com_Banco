package br.com.bank.domain.conta.DadosAberturaConta;

import br.com.bank.domain.cliente.DadosCadastroCliente;

public record DadosAberturaConta(Integer numero, DadosCadastroCliente dadosCliente) {
}