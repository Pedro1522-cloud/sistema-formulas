
const campo = (id) => document.getElementById(id);

const estado = {
    produtos: [],
    componentes: [],
    produtoId: null,
    editandoId: null
};

function moeda(valor) {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL"
    }).format(Number(valor));
}

function mensagem(texto, erro = false) {
    const caixa = campo("mensagem");
    caixa.textContent = texto;
    caixa.className = erro ? "mensagem erro" : "mensagem";
    caixa.hidden = false;
}

function mostrarErro(erro) {
    mensagem(erro.message || "Erro inesperado.", true);
}

async function api(url, opcoes = {}) {
    const resposta = await fetch(url, {
        ...opcoes,
        headers: {
            ...(opcoes.body
                ? {"Content-Type": "application/json"}
                : {}),
            ...opcoes.headers
        }
    });

    if (!resposta.ok) {
        const dados = await resposta.json().catch(() => null);

        throw new Error(
            (dados?.message || dados?.detail ||
                dados?.error || "Falha na requisição")
            + " (HTTP " + resposta.status + ")"
        );
    }

    if (resposta.status === 204) {
        return null;
    }

    return resposta.json();
}

// PRODUTOS

async function carregarProdutos(preferido = estado.produtoId) {
    estado.produtos = await api("/api/produtos");

    const encontrado = estado.produtos.find(
        p => p.id === Number(preferido)
    );

    estado.produtoId = encontrado
        ? encontrado.id
        : (estado.produtos[0]?.id ?? null);

    const seletor = campo("produtoSelecionado");
    seletor.replaceChildren();

    if (estado.produtos.length === 0) {
        seletor.add(new Option("Nenhum produto cadastrado", ""));
    }

    for (const produto of estado.produtos) {
        seletor.add(new Option(
            `${produto.nome} (ID ${produto.id})`,
            produto.id
        ));
    }

    seletor.value = estado.produtoId ?? "";

    atualizarProdutoSelecionado();
    limparEdicaoComponente();
    limparFormula();

    await carregarComponentes();
}

function atualizarProdutoSelecionado() {
    const produto = estado.produtos.find(
        p => p.id === estado.produtoId
    );

    campo("editarProdutoNome").value = produto?.nome ?? "";
    campo("editarProdutoDescricao").value =
        produto?.descricao ?? "";

    campo("atualizarProduto").disabled = !produto;
    campo("excluirProduto").disabled = !produto;
}

async function cadastrarProduto(evento) {
    evento.preventDefault();

    const produto = await api("/api/produtos", {
        method: "POST",
        body: JSON.stringify({
            nome: campo("novoProdutoNome").value.trim(),
            descricao: campo("novoProdutoDescricao").value.trim()
        })
    });

    campo("novoProdutoForm").reset();
    await carregarProdutos(produto.id);
    mensagem("Produto cadastrado com sucesso!");
}

async function atualizarProduto() {
    if (!estado.produtoId) return;

    await api(`/api/produtos/${estado.produtoId}`, {
        method: "PUT",
        body: JSON.stringify({
            nome: campo("editarProdutoNome").value.trim(),
            descricao: campo("editarProdutoDescricao").value.trim()
        })
    });

    await carregarProdutos(estado.produtoId);
    mensagem("Produto atualizado com sucesso!");
}

async function excluirProduto() {
    if (!estado.produtoId) return;

    if (estado.componentes.length > 0) {
        throw new Error(
            "Exclua os componentes antes de excluir o produto."
        );
    }

    if (!confirm("Deseja realmente excluir este produto?")) {
        return;
    }

    await api(`/api/produtos/${estado.produtoId}`, {
        method: "DELETE"
    });

    estado.produtoId = null;
    await carregarProdutos();
    mensagem("Produto excluído com sucesso!");
}

// COMPONENTES

async function carregarComponentes() {
    const todos = await api("/api/componentes");

    estado.componentes = estado.produtoId
        ? todos.filter(c => c.produtoId === estado.produtoId)
        : [];

    preencherComponentesPai();
    mostrarComponentes();
}

function preencherComponentesPai() {
    const seletor = campo("componentePai");
    seletor.replaceChildren(
        new Option("Diretamente no produto", "")
    );

    for (const componente of estado.componentes) {
        seletor.add(new Option(
            `${componente.nome} (ID ${componente.id})`,
            componente.id
        ));
    }
}

function limparEdicaoComponente() {
    estado.editandoId = null;

    campo("componenteForm").reset();
    campo("componenteQuantidade").value = 1;
    campo("componenteValor").value = 0;
    campo("componentePai").disabled = false;
    campo("atualizarPreco").disabled = true;
    campo("salvarComponente").textContent =
        "Cadastrar componente";

    preencherComponentesPai();
}

function editarComponente(componente) {
    estado.editandoId = componente.id;

    campo("componenteNome").value = componente.nome;
    campo("componenteQuantidade").value =
        componente.quantidade;
    campo("componenteValor").value =
        componente.valorUnitario;

    campo("componentePai").value =
        componente.componentePaiId ?? "";

    // O backend nao permite trocar o pai no PUT
    campo("componentePai").disabled = true;
    campo("atualizarPreco").disabled = false;

    campo("salvarComponente").textContent =
        "Salvar alterações";

    campo("componenteNome").focus();
}

function criarCelula(linha, texto) {
    const celula = linha.insertCell();
    celula.textContent = String(texto);
    return celula;
}

function criarBotao(texto, classe, acao) {
    const botao = document.createElement("button");
    botao.type = "button";
    botao.textContent = texto;
    botao.className = `pequeno ${classe}`;
    botao.addEventListener("click", () => {
        Promise.resolve()
            .then(acao)
            .catch(mostrarErro);
    });
    return botao;
}

function mostrarComponentes() {
    const tabela = campo("tabelaComponentes");
    tabela.replaceChildren();

    if (!estado.componentes.length) {
        const linha = tabela.insertRow();
        const celula = linha.insertCell();
        celula.colSpan = 5;
        celula.textContent = "Nenhum componente cadastrado.";
        return;
    }

    for (const componente of estado.componentes) {
        const linha = tabela.insertRow();

        const pai = estado.componentes.find(
            c => c.id === componente.componentePaiId
        );

        criarCelula(linha, componente.nome);
        criarCelula(linha, pai?.nome ?? "Produto principal");
        criarCelula(linha, componente.quantidade);
        criarCelula(linha, moeda(componente.valorUnitario));

        const acoes = linha.insertCell();

        acoes.append(
            criarBotao("Editar", "secundario", () => {
                editarComponente(componente);
            }),
            criarBotao("Excluir", "perigo", () => {
                return excluirComponente(componente);
            })
        );
    }
}

async function salvarComponente(evento) {
    evento.preventDefault();

    if (!estado.produtoId) {
        throw new Error("Cadastre primeiro um produto.");
    }

    const dados = {
        produtoId: estado.produtoId,
        componentePaiId: campo("componentePai").value
            ? Number(campo("componentePai").value)
            : null,
        nome: campo("componenteNome").value.trim(),
        quantidade: Number(campo("componenteQuantidade").value),
        valorUnitario: Number(campo("componenteValor").value)
    };

    const editando = estado.editandoId !== null;

    const url = editando
        ? `/api/componentes/${estado.editandoId}`
        : "/api/componentes";

    await api(url, {
        method: editando ? "PUT" : "POST",
        body: JSON.stringify(dados)
    });

    limparEdicaoComponente();
    await carregarComponentes();
    limparFormula();

    mensagem(editando
        ? "Componente atualizado!"
        : "Componente cadastrado!");
}

async function atualizarSomentePreco() {
    if (estado.editandoId === null) return;

    await api(
        `/api/componentes/${estado.editandoId}/preco`,
        {
            method: "PATCH",
            body: JSON.stringify({
                valorUnitario: Number(
                    campo("componenteValor").value
                )
            })
        }
    );

    limparEdicaoComponente();
    await carregarComponentes();

    // Recalcula a formula apos alterar o preco
    await calcularFormula();

    mensagem("Preço atualizado e custo recalculado!");
}

async function excluirComponente(componente) {
    const possuiFilhos = estado.componentes.some(
        c => c.componentePaiId === componente.id
    );

    if (possuiFilhos) {
        throw new Error(
            "Exclua primeiro os subcomponentes."
        );
    }

    if (!confirm(`Excluir ${componente.nome}?`)) {
        return;
    }

    await api(`/api/componentes/${componente.id}`, {
        method: "DELETE"
    });

    limparEdicaoComponente();
    await carregarComponentes();
    limparFormula();

    mensagem("Componente excluído!");
}

// FORMULA, ARVORE E EXPLOSAO

function limparFormula() {
    campo("resultadoFormula").hidden = true;
    campo("arvoreVisual").replaceChildren();
    campo("tabelaExplosao").replaceChildren();
}

function desenharArvore(no) {
    const lista = document.createElement("ul");
    lista.className = "arvore";

    const linha = document.createElement("li");

    const nome = no.valor.nome;
    const quantidade = no.valor.quantidade;

    let texto = `${nome} (quantidade: ${quantidade})`;

    if (!no.filhos.length) {
        texto += ` — ${moeda(no.valor.valorUnitario)}`;
    }

    linha.append(document.createTextNode(texto));

    for (const filho of no.filhos) {
        linha.append(desenharArvore(filho));
    }

    lista.append(linha);

    return lista;
}

async function calcularFormula() {
    if (!estado.produtoId) {
        throw new Error("Selecione um produto.");
    }

    const quantidade = Number(
        campo("quantidadeProduzir").value
    );

    if (!Number.isInteger(quantidade) || quantidade <= 0) {
        throw new Error("Informe uma quantidade inteira positiva.");
    }

    const dados = await api(
        `/api/formulas/${estado.produtoId}?quantidade=${quantidade}`
    );

    campo("resultadoNome").textContent = dados.produto;
    campo("resultadoUnitario").textContent =
        moeda(dados.custoUnitario);
    campo("resultadoTotal").textContent =
        moeda(dados.custoProducao);

    campo("arvoreVisual").replaceChildren(
        desenharArvore(dados.arvore)
    );

    const tabela = campo("tabelaExplosao");
    tabela.replaceChildren();

    for (const item of dados.explosao) {
        const linha = tabela.insertRow();

        const celulaNome = criarCelula(linha, item.nome);
        celulaNome.title = item.caminho;

        criarCelula(linha, item.quantidadeNecessaria);
        criarCelula(linha, moeda(item.custoAquisicao));
    }

    campo("resultadoFormula").hidden = false;
}

// INICIALIZACAO E EVENTOS

document.addEventListener("DOMContentLoaded", () => {

    campo("novoProdutoForm").addEventListener(
        "submit",
        evento => cadastrarProduto(evento).catch(mostrarErro)
    );

    campo("atualizarProduto").addEventListener(
        "click",
        () => atualizarProduto().catch(mostrarErro)
    );

    campo("excluirProduto").addEventListener(
        "click",
        () => excluirProduto().catch(mostrarErro)
    );

    campo("produtoSelecionado").addEventListener(
        "change",
        async evento => {
            try {
                estado.produtoId = Number(evento.target.value);

                atualizarProdutoSelecionado();
                limparEdicaoComponente();
                limparFormula();

                await carregarComponentes();
            } catch (erro) {
                mostrarErro(erro);
            }
        }
    );

    campo("componenteForm").addEventListener(
        "submit",
        evento => salvarComponente(evento).catch(mostrarErro)
    );

    campo("cancelarEdicao").addEventListener(
        "click",
        limparEdicaoComponente
    );

    campo("atualizarPreco").addEventListener(
        "click",
        () => atualizarSomentePreco().catch(mostrarErro)
    );

    campo("formulaForm").addEventListener(
        "submit",
        evento => {
            evento.preventDefault();
            calcularFormula().catch(mostrarErro);
        }
    );

    carregarProdutos().catch(mostrarErro);
});
