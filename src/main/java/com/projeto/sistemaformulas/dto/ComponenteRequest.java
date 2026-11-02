package com.projeto.sistemaformulas.dto;

import java.math.BigDecimal;

public record ComponenteRequest(
        Long produtoId,
        Long componentePaiId,
        String nome,
        Integer quantidade,
        BigDecimal valorUnitario
) {
}
