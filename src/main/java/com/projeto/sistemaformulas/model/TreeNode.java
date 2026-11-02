
package com.projeto.sistemaformulas.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {

    private T valor;

    private List<TreeNode<T>> filhos;

    public TreeNode(T valor) {
        this.valor = valor;
        this.filhos = new ArrayList<>();
    }

    public T getValor() {
        return valor;
    }

    public List<TreeNode<T>> getFilhos() {
        return filhos;
    }

    public void adicionarFilho(TreeNode<T> filho) {
        filhos.add(filho);
    }
}
