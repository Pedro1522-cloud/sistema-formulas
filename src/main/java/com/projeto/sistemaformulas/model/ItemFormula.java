
package com.projeto.sistemaformulas.model;

public class ItemFormula {

    private String nome;
    private String pai;
    private int quantidade;
    private double valorUnitario;

    public ItemFormula(
            String nome,
            String pai,
            int quantidade,
            double valorUnitario) {

        this.nome = nome;
        this.pai = pai;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public String getNome() {
        return nome;
    }

    public String getPai() {
        return pai;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
