package com.autoshopping.stock_control.api.loja;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface LojasRepository extends CrudRepository<Lojas, String> {

    Optional<Lojas> getLojasById(BigInteger id);

    Optional<Lojas> findByDescricaoIgnoreCase(String descricao);

}
