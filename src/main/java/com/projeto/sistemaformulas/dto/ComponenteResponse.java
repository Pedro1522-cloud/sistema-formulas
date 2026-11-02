package com.projeto.sistemaformulas.dto;

import java.math.BigDecimal;

import com.projeto.sistemaformulas.entity.Componente;

public record ComponenteResponse(
        Long id,
        String nome,
        Integer quantidade,
        BigDecimal valorUnitario,
        Long produtoId,
        Long componentePaiId
) {

    // Converte uma entidade em DTO de resposta
    public static ComponenteResponse fromEntity(
            Componente componente) {

        Long paiId = null;

        // Verifica se o componente possui pai
        if (componente.getComponentePai() != null) {
            paiId = componente.getComponentePai().getId();
        }

        return new ComponenteResponse(
                componente.getId(),
                componente.getNome(),
                componente.getQuantidade(),
                componente.getValorUnitario(),
                componente.getProduto().getId(),
                paiId
        );
    }
}
