package com.projeto.sistemaformulas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.sistemaformulas.entity.Componente;

public interface ComponenteRepository
        extends JpaRepository<Componente, Long> {

    // Buscar componentes de um produto
    List<Componente> findByProduto_IdOrderByIdAsc(
            Long produtoId);

    // Verificar se um componente possui filhos
    boolean existsByComponentePai_Id(Long componentePaiId);
}
