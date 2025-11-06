package com.autoshopping.stock_control.api.vendedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/vendedor")
public class VendedorController {

    private static final Logger logger = LoggerFactory.getLogger(VendedorController.class);

    @Autowired
    private VendedorService service;
    private VendedorRepository vendedorRepository;

    @Autowired
    public VendedorController(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }


    @GetMapping
    public ResponseEntity<Iterable<Vendedor>> get(){return ResponseEntity.ok(service.getVendedor());}

    @GetMapping("/nome/{nome}")
    public Optional<Vendedor> getVendedorByNome(@PathVariable("nome") String nome){
        logger.info("Consulta ao cadastro do vendedor {}", nome, "realizada.");
        return service.getVendedorByNome(nome);
    }

    @GetMapping("/unidade/{unidade}")
    public Iterable<Vendedor> getVendedorByUnidade(@PathVariable String unidade) {
        System.out.println("Consulta ao cadastro dos vendedores da loja: " + unidade);
        logger.info("Consulta ao cadastro dos vendedores da loja: {}", unidade);
        return vendedorRepository.findByUnidade(unidade);
    }

    @DeleteMapping("/nome/{nome}")
    public ResponseEntity delete (@PathVariable("nome") String nome){
        boolean ok=service.delete(nome);
        return ok?
                ResponseEntity.ok("Registro deletado com successo"):
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity post(@RequestBody Vendedor vendedor){
        Vendedor novo =service.insert(vendedor);
        logger.info("Foi realizada um novo cadastro de vendedor: {}", vendedor);
        return ResponseEntity.ok("Vendedor cadastrado com sucesso.");
    }

}

