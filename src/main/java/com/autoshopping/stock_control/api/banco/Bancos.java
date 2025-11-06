package com.autoshopping.stock_control.api.banco;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table (name = "banco")
public class Bancos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String descricao;
    private String cnpj;
    private String agente;
    private String email;
    private String telefone;
}
