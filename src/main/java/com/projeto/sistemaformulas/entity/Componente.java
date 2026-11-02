package com.projeto.sistemaformulas.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "componentes")
public class Componente {

    // Identificador único do componente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do componente
    @Column(nullable = false, length = 120)
    private String nome;

    // Quantidade necessária
    @Column(nullable = false)
    private Integer quantidade;

    // Valor unitário do componente
    @Column(
        name = "valor_unitario",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal valorUnitario;

    // Produto ao qual o componente pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Componente pai na árvore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "componente_pai_id")
    private Componente componentePai;

    // Construtor vazio exigido pelo JPA
    public Componente() {
    }

    // Construtor para cadastrar componentes
    public Componente(
            String nome,
            Integer quantidade,
            BigDecimal valorUnitario,
            Produto produto,
            Componente componentePai) {

        this.nome = nome;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.produto = produto;
        this.componentePai = componentePai;
    }

    // Getters e Setters

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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Componente getComponentePai() {
        return componentePai;
    }

    public void setComponentePai(Componente componentePai) {
        this.componentePai = componentePai;
    }
}
