package com.projeto.sistemaformulas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.sistemaformulas.entity.Componente;
import com.projeto.sistemaformulas.entity.Produto;
import com.projeto.sistemaformulas.model.ItemFormula;
import com.projeto.sistemaformulas.model.TreeNode;
import com.projeto.sistemaformulas.repository.ComponenteRepository;
import com.projeto.sistemaformulas.repository.ProdutoRepository;

@Service
public class ArvoreBancoService {

    // Repositórios do banco H2
    private final ProdutoRepository produtoRepository;
    private final ComponenteRepository componenteRepository;

    // Construtor com injeção de dependência
    public ArvoreBancoService(
            ProdutoRepository produtoRepository,
            ComponenteRepository componenteRepository) {

        this.produtoRepository = produtoRepository;
        this.componenteRepository = componenteRepository;
    }

    // Montar a árvore utilizando o banco H2
    @Transactional(readOnly = true)
    public TreeNode<ItemFormula> montarArvore(Long produtoId) {

        // 1. Buscar o produto no banco
        Produto produto = produtoRepository
                .findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + produtoId));

        // 2. Buscar os componentes do produto
        List<Componente> componentes =
                componenteRepository
                        .findByProduto_IdOrderByIdAsc(produtoId);

        // 3. Criar a raiz da árvore
        ItemFormula itemPrincipal = new ItemFormula(
                produto.getNome(),
                null,
                1,
                0.0
        );

        TreeNode<ItemFormula> raiz =
                new TreeNode<>(itemPrincipal);

        // 4. Criar um mapa para identificar os nós
        Map<Long, TreeNode<ItemFormula>> mapa =
                new HashMap<>();

        // 5. Criar os nós de todos os componentes
        for (Componente componente : componentes) {

            String nomePai = produto.getNome();

            if (componente.getComponentePai() != null) {
                nomePai = componente.getComponentePai().getNome();
            }

            ItemFormula item = new ItemFormula(
                    componente.getNome(),
                    nomePai,
                    componente.getQuantidade(),
                    componente.getValorUnitario().doubleValue()
            );

            TreeNode<ItemFormula> no =
                    new TreeNode<>(item);

            mapa.put(componente.getId(), no);
        }

        // 6. Montar os relacionamentos da árvore
        for (Componente componente : componentes) {

            TreeNode<ItemFormula> noAtual =
                    mapa.get(componente.getId());

            if (componente.getComponentePai() == null) {

                // Componente pertence diretamente ao produto
                raiz.adicionarFilho(noAtual);

            } else {

                // Encontrar o componente pai
                Long paiId =
                        componente.getComponentePai().getId();

                TreeNode<ItemFormula> noPai =
                        mapa.get(paiId);

                if (noPai == null) {
                    throw new IllegalStateException(
                            "Componente pai não encontrado na árvore: "
                                    + paiId);
                }

                // Adicionar o componente ao seu pai
                noPai.adicionarFilho(noAtual);
            }
        }

        // 7. Retornar a árvore montada
        return raiz;
    }
}
