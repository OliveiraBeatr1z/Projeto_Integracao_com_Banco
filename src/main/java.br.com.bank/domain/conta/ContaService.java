package br.com.bank.domain.cliente.ClienteService;

 public class ContaService{
    private Set<Conta> contas = new HashSet<>();

     public Set<Conta> listarContasAbertas(){
         return contas;
     }

     public bigDecimal consultarSaldo (Integer numeroDaConta){
         var conta = buscarContaPorNumero(numeroDaConta);
         return conta.getSaldo();
     }

     public void abrir (DadosAberturaConta dadosDaConta){
         var cliente = new Cliente(dadosDaConta.dadosCliente());
         var conta = new Conta(dadosDaConta.numero(), cliente);
         if (contas.contains(conta)){
             throw new RegraDeNegocioException("Já existe uma conta aberta com o mesmo número!");
         }

         contas.add(conta);
     }

     public void realizarSaque(Integer numeroDaConta, BigDecimal valor){
         var conta = buscarContaPorNumero(numeroDaConta);
         if(valor.compareTo(BigDecimal.ZERO) <= 0) {
             throw new RegraDeNegocioException("O valor do saque deve ser maior que zero!");
         }

         if(valor.compareTo(conta.getSaldo()) > 0){
             throw new RegraDeNegocioException("Saldo insuficiente!");
         }

         conta.sacar(valor);

     }

     public void realizarDeposito (Integer numeroDaConta, BigDecimal valor){
         var conta = buscarContaPorNumero(numeroDaConta);
         if(valor.compareTo(BigDecimal.ZERO) <= 0){
             throw new RegraDeNegocioException("O valor do depósito deve ser maior que zero!");
         }
     }

        public void encerrar(Integer numeroDaConta){
            var conta = buscarContaPorNumero(numeroDaConta);
            if(conta.possuiSaldo()){
                throw RegraDeNegocio("Conta não pode ser encerrada pois ainda possui saldo!");
            }
        }
        public Conta buscarContaPorNumero (Integer numeroDaConta){
            return contas
                    .stream()
                    .filter(c -> c.getNumero() == numero)
                    .findFirst()
                    .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
        }
 }