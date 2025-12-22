package com.autoshopping.stock_control.api.pulmao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PulmaoService {

    @Autowired
    private PulmaoRepository rep;

    private static final Logger logger = LoggerFactory.getLogger(PulmaoService.class);

    private Iterable<Pulmao> optional;
    private Integer id_unidade;

    public Iterable<Pulmao> getPulmao() {
        return rep.findAll();
    }

    public Iterable<Pulmao> getPulmaoByUnidade(String unidade) {
        return rep.findByUnidade(unidade);
    }

    public Optional<Pulmao> getPulmaoByModelo(String modelo) {
        return rep.findByModelo(modelo);
    }

    Optional<Pulmao> getPulmaoById(Integer id) {
        return rep.findById(id);
    }

    public Optional<Pulmao> getPulmaoByPlaca(String placa) {
        return rep.getPulmaoByPlaca(placa);
    }

    /*Metodo para salvar um veículo*/
    public Pulmao insert(Pulmao pulmao) {return rep.save(pulmao);}

    /*Metodo para atualizar um veículo*/
    public Pulmao update(Pulmao pulmao, String placa) {
        Optional<Pulmao> optional = getPulmaoByPlaca(placa);
        if (optional.isPresent()) {
            Pulmao veiculos = optional.get();
            rep.save(pulmao);
            return pulmao;
        } else {
            throw new RuntimeException("Nao foi possivel atualizar o registro");
        }
    }

    /*Metodo para deletar um veículo*/
    public boolean delete(String placa) {
        if (getPulmaoByPlaca(placa).isPresent()) {
            rep.deleteByPlaca(placa);
            return true;
        }
        return false;
    }

}




	