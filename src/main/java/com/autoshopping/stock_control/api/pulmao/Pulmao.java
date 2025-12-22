package com.autoshopping.stock_control.api.pulmao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "pulmao",uniqueConstraints = {@UniqueConstraint(name = "uk_placa__veiculo", columnNames = {"placa"})})

public class Pulmao extends RepresentationModel<Pulmao> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Timestamp data_registro;
    private Timestamp data_alteracao;
    private String marca;
    private String modelo;
    private String cor;
    private String placa;
    private String ano_fabricacao;
    private String ano_modelo;
    private String renavan;
    private String unidade;
    private String observacao;
    private String fipe;

    public void save(Pulmao veiculo) {
    }

    public boolean isEmpty() {
        return false;
    }

    public Object get() {
        return null;
    }

}