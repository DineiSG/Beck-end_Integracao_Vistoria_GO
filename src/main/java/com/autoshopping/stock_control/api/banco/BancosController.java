package com.autoshopping.stock_control.api.banco;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/banco")
public class BancosController {

    private static final Logger logger= LoggerFactory.getLogger(BancosController.class);

    @Autowired
    private BancosService service;

    //Buscando todos os bancos
    @GetMapping
    public ResponseEntity < Iterable <Bancos>> get(){return ResponseEntity.ok(service.getBancos());}

    //Buscando um banco pelo ID
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Integer id){
        Optional<Bancos> banco=service.getBancosById(id);
        return banco
                .map(Bancos -> ResponseEntity.ok(banco))
                .orElse(ResponseEntity.notFound().build());
    }

    //Buscando um banco pelo nome
    @GetMapping("/descricao/{descricao}")
    public ResponseEntity getBancosByDescricao(@PathVariable("descricao") String descricao){
        Optional <Bancos> banco = service.getBancosByDescricao(descricao);
        return banco
                .map (Bancos ->ResponseEntity.ok(banco))
                .orElse(ResponseEntity.notFound().build());
    }

    //Buscando um banco pelo nome do agente cadastrado
    @GetMapping("/agente/{agente}")
    public ResponseEntity getBancosByAgente(@PathVariable("agente") String agente){
        Optional <Bancos> banco = service.getBancosByAgente(agente);
        return banco
                .map (Bancos -> ResponseEntity.ok(agente))
                .orElse(ResponseEntity.notFound().build());
    }

    //Buscando um banco pelo cnpj
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity getBancosByCnpj(@PathVariable("cnpj") String cnpj){
        Optional <Bancos> banco = service.getBancosByCnpj(cnpj);
        return banco
                .map (Bancos -> ResponseEntity.ok(cnpj))
                .orElse(ResponseEntity.notFound().build());
    }

    //Cadastrando um novo banco
    @PostMapping
    public ResponseEntity post (@RequestBody Bancos banco){
        Bancos novoBanco = service.insert(banco);
        return ResponseEntity.ok("Banco cadastrado com sucesso");
    }

    //Atualizando os dados de cadastro de um banco
    @PutMapping("/descricao/{descricao}")
    public ResponseEntity put(@PathVariable("descricao") String descricao, @RequestBody Bancos banco){
        Bancos atualizarBanco = service.update(banco, descricao);
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem","Cadastro do banco " + descricao + "atualizado com sucesso.");
        response.put ("banco", atualizarBanco);
        return ResponseEntity.ok(response);
    }

    //Apagando o cadastro de um banco
    @Transactional
    @DeleteMapping ("/descricao/{descricao}")
    public ResponseEntity delete(@PathVariable("descricao") String descricao){
        boolean ok=service.delete(descricao);
        return ok?
               ResponseEntity.ok("Cadastro do banco " + descricao + "removido do banco de dados."):
               ResponseEntity.notFound().build();
    }
}
