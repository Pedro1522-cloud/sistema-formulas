package com.projeto.sistemaformulas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.sistemaformulas.model.ItemFormula;
import com.projeto.sistemaformulas.model.TreeNode;

@Service
public class FormulaService {

    // 1. EXIBIR A ARVORE
    public void printTree(
            TreeNode<ItemFormula> no,
            int nivel) {

        System.out.println(
                "  ".repeat(nivel)
                + no.getValor().getNome());

        for (TreeNode<ItemFormula> filho : no.getFilhos()) {
            printTree(filho, nivel + 1);
        }
    }

    // 2. CALCULO ORIGINAL
    public double calcularTotal(TreeNode<ItemFormula> no) {

        if (no.getFilhos().isEmpty()) {
            return no.getValor().getValorUnitario();
        }

        double total = 0;

        for (TreeNode<ItemFormula> filho : no.getFilhos()) {

            int quantidade =
                    filho.getValor().getQuantidade();

            total += quantidade * calcularTotal(filho);
        }

        return total;
    }

    // 3. EXPLOSAO ORIGINAL NO TERMINAL
    public void calcularExplosao(
            TreeNode<ItemFormula> no,
            int quantidadeNecessaria,
            String caminho) {

        if (no.getFilhos().isEmpty()) {

            double custo = quantidadeNecessaria *
                    no.getValor().getValorUnitario();

            System.out.printf(
                    "%s | Quantidade: %d | Custo: R$ %.2f%n",
                    caminho,
                    quantidadeNecessaria,
                    custo);

            return;
        }

        for (TreeNode<ItemFormula> filho : no.getFilhos()) {

            int quantidadeFilho = quantidadeNecessaria
                    * filho.getValor().getQuantidade();

            calcularExplosao(
                    filho,
                    quantidadeFilho,
                    caminho + " > " + filho.getValor().getNome()
            );
        }
    }

    // 4. CALCULO COM BIGDECIMAL
    public BigDecimal calcularTotalPreciso(
            TreeNode<ItemFormula> no) {

        if (no.getFilhos().isEmpty()) {
            return BigDecimal.valueOf(
                    no.getValor().getValorUnitario());
        }

        BigDecimal total = BigDecimal.ZERO;

        for (TreeNode<ItemFormula> filho : no.getFilhos()) {

            BigDecimal quantidade = BigDecimal.valueOf(
                    filho.getValor().getQuantidade());

            BigDecimal custoFilho =
                    calcularTotalPreciso(filho);

            total = total.add(
                    quantidade.multiply(custoFilho));
        }

        return total;
    }

    // 5. EXPLOSAO DETALHADA PARA A API
    public List<ItemExplosao> gerarExplosao(
            TreeNode<ItemFormula> raiz,
            int quantidadeProduzir) {

        if (quantidadeProduzir <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        List<ItemExplosao> resultado = new ArrayList<>();

        percorrerExplosao(
                raiz,
                quantidadeProduzir,
                raiz.getValor().getNome(),
                resultado
        );

        return resultado;
    }

    // 6. PERCORRER A ARVORE RECURSIVAMENTE
    private void percorrerExplosao(
            TreeNode<ItemFormula> no,
            long quantidade,
            String caminho,
            List<ItemExplosao> resultado) {

        // Chegou a uma materia-prima
        if (no.getFilhos().isEmpty()) {

            BigDecimal valorUnitario =
                    BigDecimal.valueOf(
                            no.getValor().getValorUnitario());

            BigDecimal custoAquisicao =
                    valorUnitario.multiply(
                            BigDecimal.valueOf(quantidade));

            resultado.add(new ItemExplosao(
                    no.getValor().getNome(),
                    caminho,
                    quantidade,
                    valorUnitario.setScale(
                            2, RoundingMode.HALF_UP),
                    custoAquisicao.setScale(
                            2, RoundingMode.HALF_UP)
            ));

            return;
        }

        // Percorrer os componentes filhos
        for (TreeNode<ItemFormula> filho : no.getFilhos()) {

            long quantidadeFilho = Math.multiplyExact(
                    quantidade,
                    filho.getValor().getQuantidade());

            percorrerExplosao(
                    filho,
                    quantidadeFilho,
                    caminho + " > " + filho.getValor().getNome(),
                    resultado
            );
        }
    }

    // 7. DTO DOS COMPONENTES DA EXPLOSAO
    public record ItemExplosao(
            String nome,
            String caminho,
            long quantidadeNecessaria,
            BigDecimal valorUnitario,
            BigDecimal custoAquisicao
    ) {
    }
}
