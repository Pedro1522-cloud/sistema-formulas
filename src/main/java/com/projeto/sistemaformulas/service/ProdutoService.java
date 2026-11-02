package com.projeto.sistemaformulas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.sistemaformulas.entity.Produto;
import com.projeto.sistemaformulas.repository.ProdutoRepository;

@Service
public class ProdutoService {

    // Repositório responsável pelo banco de dados
    private final ProdutoRepository produtoRepository;

    // Construtor com injeção de dependência
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // 1. CADASTRAR UM NOVO PRODUTO
    public Produto cadastrar(Produto produto) {

        // Verifica se o nome foi informado
        if (produto.getNome() == null ||
                produto.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório.");
        }

        // Garante que será criado um novo registro
        produto.setId(null);

        // Salva o produto no banco H2
        return produtoRepository.save(produto);
    }

    // 2. LISTAR TODOS OS PRODUTOS
    public List<Produto> listar() {

        return produtoRepository.findAll();
    }

    // 3. BUSCAR UM PRODUTO PELO ID
    public Produto buscarPorId(Long id) {

        return produtoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + id));
    }

    // 4. ATUALIZAR UM PRODUTO EXISTENTE
    public Produto atualizar(Long id, Produto dados) {

        // Busca o produto cadastrado
        Produto produto = buscarPorId(id);

        // Valida o nome informado
        if (dados.getNome() == null ||
                dados.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório.");
        }

        // Atualiza as informações
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());

        // Salva as alterações no banco H2
        return produtoRepository.save(produto);
    }

    // 5. EXCLUIR UM PRODUTO PELO ID
    public void excluir(Long id) {

        // Verifica se o produto existe
        Produto produto = buscarPorId(id);

        // Exclui o produto do banco H2
        produtoRepository.delete(produto);
    }
}
