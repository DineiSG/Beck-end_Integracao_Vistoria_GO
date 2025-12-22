package com.autoshopping.stock_control.api.loja;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "unidade")
public class Lojas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String descricao;
    private String email;
    private String telefone;
    @Column(name = "qtd_veiculos")
    private Integer qtdVeiculos;
    @Column(name = "qtd_estoque_extra")
    private Integer qtdEstoqueExtra;
}
