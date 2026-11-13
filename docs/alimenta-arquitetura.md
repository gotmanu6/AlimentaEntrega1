# Arquitetura do Alimenta

## Casos de uso e orientação a objetos

O operador registra pontos e doações por uma interface que consome a API com `fetch`.
Controllers recebem DTOs validados, delegam ao Service e definem status HTTP. Services
coordenam regras e persistência. Repositories estendem Spring Data, sem regras de negócio.
Mappers convertem explicitamente documentos e contratos. Composição e injeção por
construtor permitem substituir dependências nos testes. Records representam valores
imutáveis; documentos persistidos não são usados como contratos HTTP.

```mermaid
flowchart LR
    UI[alimenta.html] --> C[Controllers]
    C --> S[Services]
    S --> R[Repositories]
    R --> M[(MongoDB)]
    S --> MAP[Mappers]
    MAP --> DTO[Response DTOs]
    DTO --> C
```

## Modelagem NoSQL — requisitos de 2026.2

Foi adotada a opção mais exigente do enunciado: múltiplas coleções relacionadas e
documentos complexos, apesar da referência ambígua a primeiro/segundo semestre.

`pontos_coleta` contém `_id`, `nome` e `endereco` aninhado:

```json
{"_id":"ponto-exemplo","nome":"Centro comunitário",
 "endereco":{"rua":"Rua Exemplo, 100","bairro":"Centro","cidade":"Maringá","estado":"PR"}}
```

`doacoes` referencia `pontos_coleta._id` por `pontoColetaId`, com lista de subdocumentos:

```json
{"_id":"doacao-exemplo","pontoColetaId":"ponto-exemplo","doador":"Mercado demonstração",
 "validade":"2026-12-31",
 "itens":[{"nome":"Arroz","quantidade":5,"unidade":"kg"},{"nome":"Feijão","quantidade":3,"unidade":"kg"}]}
```

São representações legíveis: Spring Data grava `LocalDate` como data BSON e pode
adicionar `_class`. A API expõe `id`, não `_id`; o identificador é gerado na persistência.
Um ponto tem muitas doações. A referência evita repetir o endereço em cada lote;
os itens são embutidos porque pertencem ao lote e são consultados juntos.
A coleção legada `linguagens` não faz parte do domínio social.

## Contratos

| Recurso | Operações | Respostas |
|---|---|---|
| `/api/pontos-coleta` | GET, POST | 200 lista / 201 com Location |
| `/api/pontos-coleta/{id}` | GET, PUT, DELETE | 200 / 200 / 204 |
| `/api/doacoes` | GET, POST | 200 lista / 201 com Location |
| `/api/doacoes/{id}` | GET, PUT, DELETE | 200 / 200 / 204 |

Corpos: consulte `/v3/api-docs` ou `/docs`. POST e PUT têm DTOs independentes.
Entradas inválidas retornam 400 com `fieldErrors`, incluindo `itens[0].quantidade`
e `endereco.rua`. Recursos ausentes retornam 404; conflitos de negócio, 409.
Formato de erro: `status`, `error`, `message`, `path`, `fieldErrors`.

Validade é a menor data entre os alimentos do lote. O dia atual é aceito; datas
anteriores são bloqueadas pelo Service. Um `Clock` em America/Sao_Paulo permite testes
determinísticos. Quantidades são inteiras positivas; unidades: `kg`, `litro`, `unidade`.

## Decisões e limites

- Reutilização da arquitetura do projeto-base, sem novo framework.
- PoC local de operador único, sem autenticação ou cadastro de famílias.
- Até 20 itens por lote. Não há avaliação sanitária automática ou impacto social medido.
- MongoDB não tem chave estrangeira: a aplicação valida a existência do ponto.
  Checagem de vínculos e exclusão do ponto são operações separadas. Não há garantia
  transacional entre coleções sob exclusão/criação concorrentes. Uma implantação com
  vários operadores deve usar transações ou desativação de pontos.

## Segunda entrega: ciclo do lote

```mermaid
stateDiagram-v2
    [*] --> DISPONIVEL: cadastro
    DISPONIVEL --> RESERVADA: organização beneficiária
    RESERVADA --> ENTREGUE: confirmação de entrega
```

Campos adicionados em `doacoes`: `status`, `beneficiario`, `historico` (lista de
subdocumentos com `status` e `instante`) e `versao` (Long, `@Version`). O Mapper
preserva ID e versão nas substituições imutáveis. Spring Data incrementa a versão
ao salvar; gravações e exclusões com uma versão desatualizada produzem 409.
Essa proteção é de um documento e não equivale a uma transação entre coleções.

| Operação | Contrato e regra |
|---|---|
| POST `/api/doacoes/{id}/reserva` | `{"beneficiario":"Associação exemplo"}`; exige DISPONIVEL e validade vigente |
| POST `/api/doacoes/{id}/entrega` | Sem corpo; exige RESERVADA e validade vigente |
| GET `/api/doacoes?status=RESERVADA` | Filtra a situação persistida; enum inválido retorna 400 |
| GET `/api/doacoes/resumo` | Disponíveis válidas, reservadas, entregues e vencidas |

Edição e exclusão são permitidas apenas em DISPONIVEL. Reserva e entrega retornam
200 e o DTO completo. Repetições/transições proibidas retornam 409. Uma reserva
não é autenticação nem autorização: a PoC continua restrita ao uso local.

`vencida` é calculada na resposta, sem regravar o banco: um lote não entregue e
com validade anterior ao dia atual. Reservadas vencidas aparecem no contador de
reservas e no aviso de vencimento; não podem ser entregues. O filtro DISPONIVEL
representa a situação armazenada e pode incluir lotes vencidos, sinalizados na UI.
Os totais globais não mudam ao filtrar a lista. Não se somam kg, litros e unidades
como se fossem uma mesma medida de impacto.

A migração `scripts/migrar-entrega1.js` preenche a situação DISPONIVEL, histórico
vazio e versão zero somente quando não há `status`. A integração testa execução,
preservação dos alimentos, reserva posterior e repetição sem regressão.

O cliente usa `alimenta.html`, `alimenta-http.js` e `alimenta-confirmacao.js`, todos nativos. O pequeno módulo
HTTP foi separado para testar serialização, respostas 204, erros de campo, conflitos,
falhas de rede e atualização de lista sem adicionar biblioteca ou build de frontend.
Confirmações de exclusão/entrega usam `<dialog>` acessível, com testes de confirmar,
cancelar e Escape. Uma operação só continua após confirmação; cancelar não grava dados.

## Limites assumidos

Não há autenticação, cancelamento de reserva, entregas parciais, avaliação sanitária,
notificações ou paginação. Reservas vencidas permanecem visíveis para análise manual;
um fluxo auditável de cancelamento é evolução futura. O protótipo demonstra coordenação
de lotes inteiros, não a operação completa de um banco de alimentos em produção.
