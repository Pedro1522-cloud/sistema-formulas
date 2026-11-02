package com.projeto.sistemaformulas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.sistemaformulas.dto.ComponenteRequest;
import com.projeto.sistemaformulas.dto.ComponenteResponse;
import com.projeto.sistemaformulas.entity.Componente;
import com.projeto.sistemaformulas.entity.Produto;
import com.projeto.sistemaformulas.repository.ComponenteRepository;
import com.projeto.sistemaformulas.repository.ProdutoRepository;

@Service
public class ComponenteService {

    private final ComponenteRepository componenteRepository;
    private final ProdutoRepository produtoRepository;

    public ComponenteService(
            ComponenteRepository componenteRepository,
            ProdutoRepository produtoRepository) {

        this.componenteRepository = componenteRepository;
        this.produtoRepository = produtoRepository;
    }

    // VALIDAR OS DADOS
    private void validar(
            String nome,
            Integer quantidade,
            BigDecimal valorUnitario) {

        if (nome == null || nome.isBlank()
                || nome.length() > 120) {
            throw new IllegalArgumentException(
                    "Informe um nome de ate 120 caracteres.");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        if (valorUnitario == null
                || valorUnitario.signum() < 0
                || valorUnitario.scale() > 2
                || valorUnitario.precision()
                   - valorUnitario.scale() > 10) {

            throw new IllegalArgumentException(
                    "Informe um preco valido com ate 2 decimais.");
        }
    }

    // 1. CADASTRAR
    @Transactional
    public ComponenteResponse cadastrar(
            Long produtoId,
            Long componentePaiId,
            String nome,
            Integer quantidade,
            BigDecimal valorUnitario) {

        validar(nome, quantidade, valorUnitario);

        if (produtoId == null) {
            throw new IllegalArgumentException(
                    "O produto e obrigatorio.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto nao encontrado: " + produtoId));

        Componente pai = null;

        if (componentePaiId != null) {

            pai = componenteRepository.findById(componentePaiId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Componente pai nao encontrado."));

            if (!pai.getProduto().getId().equals(produtoId)) {
                throw new IllegalArgumentException(
                        "O pai pertence a outro produto.");
            }
        }

        Componente componente = new Componente(
                nome, quantidade, valorUnitario,
                produto, pai);

        return ComponenteResponse.fromEntity(
                componenteRepository.save(componente));
    }

    // 2. LISTAR
    @Transactional(readOnly = true)
    public List<ComponenteResponse> listar() {

        return componenteRepository.findAll()
                .stream()
                .map(ComponenteResponse::fromEntity)
                .toList();
    }

    // 3. BUSCAR POR ID
    @Transactional(readOnly = true)
    public ComponenteResponse buscarPorId(Long id) {

        Componente componente = componenteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Componente nao encontrado: " + id));

        return ComponenteResponse.fromEntity(componente);
    }

    // 4. ATUALIZAR PRECO
    @Transactional
    public ComponenteResponse atualizarPreco(
            Long id, BigDecimal novoPreco) {

        Componente componente = componenteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Componente nao encontrado: " + id));

        validar(
                componente.getNome(),
                componente.getQuantidade(),
                novoPreco);

        componente.setValorUnitario(novoPreco);

        return ComponenteResponse.fromEntity(
                componenteRepository.save(componente));
    }

    // 5. ATUALIZAR COMPONENTE
    @Transactional
    public ComponenteResponse atualizar(
            Long id, ComponenteRequest dados) {

        Componente componente = componenteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Componente nao encontrado: " + id));

        validar(
                dados.nome(),
                dados.quantidade(),
                dados.valorUnitario());

        Long produtoAtual = componente.getProduto().getId();

        Long paiAtual = componente.getComponentePai() == null
                ? null
                : componente.getComponentePai().getId();

        // Preservar os relacionamentos da arvore
        if (!Objects.equals(produtoAtual, dados.produtoId())
                || !Objects.equals(paiAtual, dados.componentePaiId())) {

            throw new IllegalArgumentException(
                    "Nao e permitido trocar o produto ou pai nesta operacao.");
        }

        componente.setNome(dados.nome());
        componente.setQuantidade(dados.quantidade());
        componente.setValorUnitario(dados.valorUnitario());

        return ComponenteResponse.fromEntity(
                componenteRepository.save(componente));
    }

    // 6. EXCLUIR COMPONENTE
    @Transactional
    public void excluir(Long id) {

        Componente componente = componenteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Componente nao encontrado: " + id));

        // Impedir excluir um no que possui filhos
        if (componenteRepository.existsByComponentePai_Id(id)) {
            throw new IllegalStateException(
                    "Exclua os subcomponentes antes do componente pai.");
        }

        componenteRepository.delete(componente);
    }
}
