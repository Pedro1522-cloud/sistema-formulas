package com.projeto.sistemaformulas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.projeto.sistemaformulas.dto.ComponenteRequest;
import com.projeto.sistemaformulas.dto.ComponenteResponse;
import com.projeto.sistemaformulas.dto.AtualizacaoPrecoRequest;
import com.projeto.sistemaformulas.service.ComponenteService;

@RestController
@RequestMapping("/api/componentes")
public class ComponenteController {

    private final ComponenteService componenteService;

    public ComponenteController(
            ComponenteService componenteService) {

        this.componenteService = componenteService;
    }

    // 1. CADASTRAR
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComponenteResponse cadastrar(
            @RequestBody ComponenteRequest request) {

        return componenteService.cadastrar(
                request.produtoId(),
                request.componentePaiId(),
                request.nome(),
                request.quantidade(),
                request.valorUnitario());
    }

    // 2. LISTAR
    @GetMapping
    public List<ComponenteResponse> listar() {
        return componenteService.listar();
    }

    // 3. BUSCAR PELO ID
    @GetMapping("/{id}")
    public ComponenteResponse buscarPorId(
            @PathVariable Long id) {

        return componenteService.buscarPorId(id);
    }

    // 4. ATUALIZAR PRECO
    @PatchMapping("/{id}/preco")
    public ComponenteResponse atualizarPreco(
            @PathVariable Long id,
            @RequestBody AtualizacaoPrecoRequest request) {

        return componenteService.atualizarPreco(
                id, request.valorUnitario());
    }

    // 5. ATUALIZAR COMPONENTE
    @PutMapping("/{id}")
    public ComponenteResponse atualizar(
            @PathVariable Long id,
            @RequestBody ComponenteRequest request) {

        return componenteService.atualizar(id, request);
    }

    // 6. EXCLUIR COMPONENTE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        componenteService.excluir(id);
    }
}
