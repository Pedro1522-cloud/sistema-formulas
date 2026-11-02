package com.projeto.sistemaformulas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.projeto.sistemaformulas.entity.Produto;
import com.projeto.sistemaformulas.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    // Serviço responsável pelas regras de negócio
    private final ProdutoService produtoService;

    // Construtor com injeção de dependência
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // 1. CADASTRAR UM NOVO PRODUTO
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(@RequestBody Produto produto) {

        return produtoService.cadastrar(produto);
    }

    // 2. LISTAR TODOS OS PRODUTOS
    @GetMapping
    public List<Produto> listar() {

        return produtoService.listar();
    }

    // 3. BUSCAR PRODUTO PELO ID
    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {

        return produtoService.buscarPorId(id);
    }

    // 4. ATUALIZAR PRODUTO PELO ID
    @PutMapping("/{id}")
    public Produto atualizar(
            @PathVariable Long id,
            @RequestBody Produto produto) {

        return produtoService.atualizar(id, produto);
    }

    // 5. EXCLUIR PRODUTO PELO ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        produtoService.excluir(id);
    }
}
