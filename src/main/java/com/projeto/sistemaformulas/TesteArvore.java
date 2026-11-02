
package com.projeto.sistemaformulas;

import java.util.Scanner;

import com.projeto.sistemaformulas.model.ItemFormula;
import com.projeto.sistemaformulas.model.TreeNode;
import com.projeto.sistemaformulas.service.FormulaService;

public class TesteArvore {

    public static void main(String[] args) {


        FormulaService formulaService = new FormulaService();

        // 2. Criando o produto principal
        ItemFormula caneta = new ItemFormula(
                "Caneta", null, 1, 2.50);


        ItemFormula tinta = new ItemFormula(
                "Tinta", "Caneta", 1, 0.80);

        ItemFormula corpo = new ItemFormula(
                "Corpo", "Caneta", 1, 1.00);

        ItemFormula tampa = new ItemFormula(
                "Tampa", "Caneta", 1, 0.70);


        ItemFormula pigmento = new ItemFormula(
                "Pigmento", "Tinta", 1, 0.30);

        ItemFormula solvente = new ItemFormula(
                "Solvente", "Tinta", 1, 0.50);


        ItemFormula plasticoCorpo = new ItemFormula(
                "Plástico", "Corpo", 1, 0.70);

        ItemFormula clip = new ItemFormula(
                "Clip", "Corpo", 1, 0.30);


        ItemFormula plasticoTampa = new ItemFormula(
                "Plástico", "Tampa", 1, 0.50);

        ItemFormula vedacao = new ItemFormula(
                "Vedação", "Tampa", 1, 0.20);


        TreeNode<ItemFormula> raiz =
                new TreeNode<>(caneta);


        TreeNode<ItemFormula> noTinta =
                new TreeNode<>(tinta);

        TreeNode<ItemFormula> noCorpo =
                new TreeNode<>(corpo);

        TreeNode<ItemFormula> noTampa =
                new TreeNode<>(tampa);


        noTinta.adicionarFilho(
                new TreeNode<>(pigmento));

        noTinta.adicionarFilho(
                new TreeNode<>(solvente));


        noCorpo.adicionarFilho(
                new TreeNode<>(plasticoCorpo));

        noCorpo.adicionarFilho(
                new TreeNode<>(clip));


        noTampa.adicionarFilho(
                new TreeNode<>(plasticoTampa));

        noTampa.adicionarFilho(
                new TreeNode<>(vedacao));


        raiz.adicionarFilho(noTinta);
        raiz.adicionarFilho(noCorpo);
        raiz.adicionarFilho(noTampa);


        System.out.println(
                "Árvore completa da fórmula da Caneta:");

        formulaService.printTree(raiz, 0);


        double total = formulaService.calcularTotal(raiz);

        System.out.printf(
                "\nCusto total da Caneta: R$ %.2f%n",
                total);


        int quantidadeProduzir;

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print(
                    "\nQuantas canetas deseja produzir? ");

            if (!scanner.hasNextInt()) {
                System.out.println(
                        "Digite um número inteiro válido.");
                return;
            }

            quantidadeProduzir = scanner.nextInt();

            if (quantidadeProduzir <= 0) {
                System.out.println(
                        "A quantidade deve ser maior que zero.");
                return;
            }
        }


        System.out.println(
                "\nExplosão dos componentes para "
                + quantidadeProduzir + " canetas:");

        formulaService.calcularExplosao(
                raiz,
                quantidadeProduzir,
                raiz.getValor().getNome());


        double custoProducao =
                total * quantidadeProduzir;

        System.out.printf(
                "\nCusto total da produção: R$ %.2f%n",
                custoProducao);


        System.out.println(
                "\n=== ATUALIZAÇÃO DE PREÇO ===");

        pigmento.setValorUnitario(0.40);

        System.out.printf(
                "Novo preço do Pigmento: R$ %.2f%n",
                pigmento.getValorUnitario());


        double novoCusto =
                formulaService.calcularTotal(raiz);

        System.out.printf(
                "Novo custo da Caneta: R$ %.2f%n",
                novoCusto);


        double novoCustoProducao =
                novoCusto * quantidadeProduzir;

        System.out.printf(
                "Novo custo da produção: R$ %.2f%n",
                novoCustoProducao);

    } // Fim do método main

} // Fim da classe TesteArvore
