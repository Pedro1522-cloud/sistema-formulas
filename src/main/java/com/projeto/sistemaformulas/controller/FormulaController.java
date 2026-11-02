package com.projeto.sistemaformulas.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.projeto.sistemaformulas.model.ItemFormula;
import com.projeto.sistemaformulas.model.TreeNode;
import com.projeto.sistemaformulas.service.ArvoreBancoService;
import com.projeto.sistemaformulas.service.FormulaService;
import com.projeto.sistemaformulas.service.FormulaService.ItemExplosao;

@RestController
@RequestMapping("/api/formulas")
public class FormulaController {

    private final ArvoreBancoService arvoreBancoService;
    private final FormulaService formulaService;

    public FormulaController(
            ArvoreBancoService arvoreBancoService,
            FormulaService formulaService) {

        this.arvoreBancoService = arvoreBancoService;
        this.formulaService = formulaService;
    }

    // CONSULTAR FORMULA COMPLETA
    @GetMapping("/{produtoId}")
    public FormulaResponse consultarFormula(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") int quantidade) {

        // 1. Validar quantidade
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        // 2. Montar arvore do banco H2
        TreeNode<ItemFormula> arvore =
                arvoreBancoService.montarArvore(produtoId);

        if (arvore.getFilhos().isEmpty()) {
            throw new IllegalStateException(
                    "O produto nao possui componentes cadastrados.");
        }

        // 3. Calcular custo unitario
        BigDecimal custoUnitario =
                formulaService.calcularTotalPreciso(arvore)
                        .setScale(2, RoundingMode.HALF_UP);

        // 4. Calcular custo da producao
        BigDecimal custoProducao =
                custoUnitario.multiply(
                        BigDecimal.valueOf(quantidade))
                        .setScale(2, RoundingMode.HALF_UP);

        // 5. Gerar explosao detalhada
        List<ItemExplosao> explosao =
                formulaService.gerarExplosao(
                        arvore,
                        quantidade
                );

        // 6. Retornar dados completos
        return new FormulaResponse(
                produtoId,
                arvore.getValor().getNome(),
                quantidade,
                custoUnitario,
                custoProducao,
                arvore,
                explosao
        );
    }

    // DTO COMPLETO DA FORMULA
    public record FormulaResponse(
            Long produtoId,
            String produto,
            int quantidade,
            BigDecimal custoUnitario,
            BigDecimal custoProducao,
            TreeNode<ItemFormula> arvore,
            List<ItemExplosao> explosao
    ) {
    }
}
