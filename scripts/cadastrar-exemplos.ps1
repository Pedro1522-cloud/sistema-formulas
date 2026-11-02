
$ErrorActionPreference = "Stop"
$base = "http://localhost:8080/api"

# Envia dados para a API REST
function Enviar-Api($rota, $dados) {
    $json = ConvertTo-Json -InputObject $dados -Depth 5

    return Invoke-RestMethod `
        -Uri "$base/$rota" `
        -Method Post `
        -ContentType "application/json; charset=utf-8" `
        -Body ([System.Text.Encoding]::UTF8.GetBytes($json))
}

# Cadastra a formula completa de um produto
function Cadastrar-Exemplo($nome, $descricao, $itens) {

     $produtos = Invoke-RestMethod -Uri "$base/produtos"

    $encontrados = @(
        $produtos | Where-Object { $_.nome -eq $nome }
    )

    if ($encontrados.Count -gt 1) {
        throw "Existem produtos duplicados: $nome"
    }

    if ($encontrados.Count -eq 1) {
        $produto = $encontrados[0]
    } else {
        $produto = Enviar-Api "produtos" @{
            nome = $nome
            descricao = $descricao
        }
    }

    $todos = Invoke-RestMethod -Uri "$base/componentes"

    $existentes = @(
        $todos | Where-Object {
            $_.produtoId -eq $produto.id
        }
    )

    if ($existentes.Count -eq $itens.Count) {
        Write-Host "$nome ja possui a formula cadastrada."
        return
    }

    if ($existentes.Count -gt 0) {
        throw "Formula parcial encontrada para $nome. Verifique antes de continuar."
    }

    # Identificadores dos componentes criados
    $ids = @{}

    foreach ($item in $itens) {

        $paiId = $null

        if ($null -ne $item.pai) {
            $paiId = $ids[$item.pai]

            if ($null -eq $paiId) {
                throw "Pai nao encontrado: $($item.pai)"
            }
        }

        $novo = Enviar-Api "componentes" @{
            produtoId = $produto.id
            componentePaiId = $paiId
            nome = $item.nome
            quantidade = $item.q
            valorUnitario = $item.valor
        }

        $ids[$item.chave] = $novo.id
    }

    Write-Host "$nome cadastrado com sucesso!"
}

# TESTE 1 - CANETA
$itensCaneta = @(
    @{chave="tinta"; pai=$null; nome="Tinta"; q=1; valor=0.80}
    @{chave="pig"; pai="tinta"; nome="Pigmento"; q=1; valor=0.30}
    @{chave="sol"; pai="tinta"; nome="Solvente"; q=1; valor=0.50}
    @{chave="corpo"; pai=$null; nome="Corpo"; q=1; valor=1.00}
    @{chave="pc"; pai="corpo"; nome="Plástico"; q=1; valor=0.70}
    @{chave="clip"; pai="corpo"; nome="Clip"; q=1; valor=0.30}
    @{chave="tampa"; pai=$null; nome="Tampa"; q=1; valor=0.70}
    @{chave="pt"; pai="tampa"; nome="Plástico"; q=1; valor=0.50}
    @{chave="ved"; pai="tampa"; nome="Vedação"; q=1; valor=0.20}
)

# TESTE 2 - COMPUTADOR
$itensComputador = @(
    @{chave="gab"; pai=$null; nome="Gabinete"; q=1; valor=800}
    @{chave="placa"; pai="gab"; nome="Placa-Mãe"; q=1; valor=500}
    @{chave="cpu"; pai="placa"; nome="CPU"; q=1; valor=1000}
    @{chave="ram"; pai="placa"; nome="Memória RAM"; q=2; valor=200}
    @{chave="chip"; pai="placa"; nome="Chipset"; q=1; valor=150}
    @{chave="fonte"; pai="gab"; nome="Fonte de Alimentação"; q=1; valor=300}
    @{chave="arm"; pai="gab"; nome="Armazenamento"; q=1; valor=0}
    @{chave="hdd"; pai="arm"; nome="HDD"; q=1; valor=250}
    @{chave="ssd"; pai="arm"; nome="SSD"; q=1; valor=400}
    @{chave="mon"; pai=$null; nome="Monitor"; q=1; valor=900}
    @{chave="lcd"; pai="mon"; nome="Tela LCD"; q=1; valor=600}
    @{chave="car"; pai="mon"; nome="Carcaça"; q=1; valor=100}
    @{chave="per"; pai=$null; nome="Periféricos"; q=1; valor=0}
    @{chave="tec"; pai="per"; nome="Teclado"; q=1; valor=100}
    @{chave="mouse"; pai="per"; nome="Mouse"; q=1; valor=80}
)

# EXECUTAR OS CADASTROS
Cadastrar-Exemplo "Caneta Azul" "Caneta esferografica azul" $itensCaneta
Cadastrar-Exemplo "Computador" "Computador completo" $itensComputador

Write-Host "Preparacao dos exemplos concluida!"
