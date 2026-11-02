
package com.projeto.sistemaformulas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "produtos")
public class Produto {

    // Identificador único do produto
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do produto
    @Column(nullable = false, length = 120)
    private String nome;

    // Descrição do produto
    private String descricao;

    // Construtor vazio exigido pelo JPA
    public Produto() {
    }

    // Construtor para cadastrar produtos
    public Produto(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Métodos Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
