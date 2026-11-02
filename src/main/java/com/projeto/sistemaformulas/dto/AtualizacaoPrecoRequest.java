package com.projeto.sistemaformulas.dto;

import java.math.BigDecimal;

public record AtualizacaoPrecoRequest(
        BigDecimal valorUnitario
) {
}
