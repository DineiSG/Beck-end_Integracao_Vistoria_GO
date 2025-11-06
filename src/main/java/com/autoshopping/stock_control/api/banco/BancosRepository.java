package com.autoshopping.stock_control.api.banco;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BancosRepository extends JpaRepository<Bancos, Integer> {


    Optional<Bancos> findByDescricao(String descricao);

    Optional<Bancos> findByAgente(String agente);

    Optional<Bancos> findByCnpj(String cnpj);

    void deleteByDescricao(String descricao);
}
