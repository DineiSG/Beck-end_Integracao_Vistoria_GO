package com.autoshopping.stock_control.api.banco;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BancosService {

    @Autowired
    private BancosRepository rep;

    private static final Logger logger = LoggerFactory.getLogger(BancosService.class);

    private Iterable<Bancos> optional;
    private Integer id_banco;

    public Optional<Bancos> getBancosById(Integer id){
        return rep.findById(id);
    };

    public Optional<Bancos> getBancosByDescricao(String descricao) {
        return rep.findByDescricao(descricao);
    }

    public Optional<Bancos> getBancosByAgente(String agente){
        return rep.findByAgente(agente);
    }

    public Optional<Bancos> getBancosByCnpj(String cnpj){
        return rep.findByCnpj(cnpj);
    }

    public Bancos insert(Bancos banco){return rep.save(banco);}

    public Bancos update(Bancos banco, String descricao){
        Optional<Bancos> optional = getBancosByDescricao(descricao);
        if(optional.isPresent()){
            Bancos bancos = optional.get();
            rep.save(banco);
            return banco;
        }else{
            throw new RuntimeException("Nao foi possivel atualizar os dados do banco.");
        }
    }

    public boolean delete(String descricao){
        if (getBancosByDescricao(descricao).isPresent()){
            rep.deleteByDescricao(descricao);
            return true;
        }
        return false;
    }


    public Iterable<Bancos> getBancos() {
        return null;
    }
}
