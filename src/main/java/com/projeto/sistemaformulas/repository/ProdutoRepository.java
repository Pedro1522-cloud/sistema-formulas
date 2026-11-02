package com.projeto.sistemaformulas.repository;

import com.projeto.sistemaformulas.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

}
