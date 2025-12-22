package com.autoshopping.stock_control.api.pulmao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PulmaoRepository extends JpaRepository<Pulmao, String> {


    Optional<Pulmao> getPulmaoByPlaca(String placa);

	Optional<Pulmao> findById(Integer id);

	Iterable<Pulmao> findByUnidade(String unidade);

	Optional<Pulmao> findByModelo(String modelo);

	Optional<Pulmao> findByPlaca(String placa);

    void deleteByPlaca(String placa);
};
