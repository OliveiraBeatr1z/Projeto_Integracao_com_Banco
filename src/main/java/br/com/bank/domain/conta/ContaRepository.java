package br.com.bank.domain.conta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    
    Optional<Conta> findByNumero(Integer numero);
    
    List<Conta> findByEstaAtivaTrue();
    
    boolean existsByNumero(Integer numero);
}

