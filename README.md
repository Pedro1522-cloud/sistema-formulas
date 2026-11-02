
# Sistema de Fórmulas de Produtos

Sistema web desenvolvido em Java e Spring Boot para
gerenciamento de fórmulas de produtos por meio de
árvores genéricas.

O sistema permite cadastrar produtos e componentes,
visualizar suas estruturas, calcular custos de produção
e atualizar os preços das matérias-primas.

## Tecnologias utilizadas

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Maven
- H2 Database

### Frontend
- HTML5
- CSS3
- JavaScript puro
- Fetch API

### Ferramentas
- Visual Studio Code
- Git e GitHub
- H2 Console

## Funcionalidades

- Cadastro, consulta, edição e exclusão de produtos
- Cadastro, consulta, edição e exclusão de componentes
- Relacionamento entre componentes e subcomponentes
- Construção de árvores genéricas
- Visualização da fórmula completa do produto
- Explosão de matérias-primas
- Cálculo da quantidade necessária para produção
- Cálculo de custos de aquisição
- Atualização de preços dos componentes
- Recálculo automático dos custos (implosão)
- Persistência de dados com H2
- Interface gráfica integrada à API REST

## Estrutura do projeto

src/main/java/com/projeto/sistemaformulas/

- controller: endpoints da API REST
- dto: objetos de entrada e saída de dados
- entity: entidades do banco de dados
- exception: tratamento de erros
- model: estruturas da árvore genérica
- repository: acesso ao banco de dados
- service: regras de negócio e cálculos

src/main/resources/

- static/index.html: interface principal
- static/style.css: estilos visuais
- static/script.js: integração com a API
- application.properties: configurações

## Como executar

### Pré-requisitos

- Java JDK 21
- Terminal PowerShell
- Navegador web

### Executar no Windows

Abra a pasta do projeto no VS Code.

No terminal PowerShell, execute:

    .\mvnw.cmd spring-boot:run

Após iniciar o Spring Boot, acesse:

http://localhost:8080/

### Banco de dados H2

Console:

http://localhost:8080/h2-console

Utilize as configurações JDBC definidas
no arquivo application.properties.

O projeto utiliza persistência local em H2.

## Endpoints da API

### Produtos

POST   /api/produtos
GET    /api/produtos
GET    /api/produtos/{id}
PUT    /api/produtos/{id}
DELETE /api/produtos/{id}

### Componentes

POST   /api/componentes
GET    /api/componentes
GET    /api/componentes/{id}
PUT    /api/componentes/{id}
PATCH  /api/componentes/{id}/preco
DELETE /api/componentes/{id}

### Fórmulas

GET /api/formulas/{produtoId}

Quantidade de produção:

GET /api/formulas/{produtoId}?quantidade=50

## Exemplos de funcionamento

### Teste 1 - Caneta

A fórmula da Caneta é formada por três grupos:

- Tinta: Pigmento e Solvente
- Corpo: Plástico e Clip
- Tampa: Plástico e Vedação

Custo original: R$ 2,50 por unidade.

Após atualizar o preço do Pigmento:

Custo unitário: R$ 2,60.

Produção de 50 canetas: R$ 130,00.

### Teste 2 - Computador

A fórmula do Computador possui os grupos:

- Gabinete
- Placa-Mãe
- CPU
- Memória RAM
- Chipset
- Fonte de Alimentação
- Armazenamento
- HDD e SSD
- Monitor
- Tela LCD e Carcaça
- Periféricos
- Teclado e Mouse

Custo original de produção: R$ 3.380,00.

## Regras de cálculo

A árvore genérica representa a estrutura
hierárquica dos componentes do produto.

A explosão percorre a árvore recursivamente,
multiplicando as quantidades necessárias.

O custo é calculado a partir das matérias-primas
finais, evitando contabilizar duas vezes os
componentes intermediários.

A implosão recalcula o custo do produto após
a atualização dos preços dos componentes.

## Persistência

Os dados são armazenados em um banco H2 local.

O arquivo físico do banco não precisa ser
versionado no GitHub.

Em uma instalação nova, produtos e componentes
devem ser cadastrados antes de calcular as fórmulas.

## Observações

Projeto acadêmico desenvolvido para demonstrar
árvores genéricas, recursividade, CRUD, APIs REST,
persistência de dados e integração frontend/backend.


## Dados de demonstração

O projeto possui um script PowerShell para cadastrar
automaticamente dois exemplos:

- Caneta Azul
- Computador

Com o Spring Boot em execução, abra um terminal
PowerShell na raiz do projeto e execute:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\cadastrar-exemplos.ps1"
```

O script verifica os produtos já cadastrados
para evitar duplicar fórmulas completas.

Em um banco novo, os exemplos serão cadastrados
automaticamente pela API REST.

O banco H2 local não é enviado ao GitHub.
