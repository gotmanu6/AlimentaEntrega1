# Rastreabilidade da AEP 2026.2

Enunciado recebido em 09/09/2026. Tema confirmado pela equipe: doação de alimentos, ODS 2.

| Requisito | Critério de aceite | Evidência |
|---|---|---|
| Problema e ODS | Público e contribuição propostos identificáveis | README e fonte ONU |
| NoSQL efetivo | Criar e recuperar documentos reais | AlimentaApiIT e Testcontainers |
| Múltiplas coleções | Persistir pontos e doações separadamente | pontos_coleta e doacoes |
| Relacionamento | Validar referência ao ponto | 404 e bloqueio de exclusão 409 |
| Objetos complexos | Persistir endereço e alimentos | testes de subdocumentos |
| Orientação a objetos | Composição e responsabilidades explícitas | Services, Mappers, Repositories, DTOs |
| Funcionalidade | Cadastrar, consultar, editar e excluir | interface e testes CRUD |
| Testes e >=70% | Build reproduzível com limiar | mvnw clean verify, JaCoCo |
| Metodologia | Quadro versionável com critérios de aceite | quadro-tarefas.md |
| Documentação | Instalar, executar e testar | README e arquitetura |
| GitHub e marcos | URL acessível e versões identificadas | commits/publicação a cargo da equipe |
| Vídeos | 2–3 min e 3–5 min | clipes de testes; demonstrações completas pendentes |

Entrega 1: CRUD, relacionamentos, documentos aninhados, validação, interface e testes.
Entrega 2: reserva, entrega, transições, concorrência, filtro e resumo operacional.
As evidências devem representar estados realmente executados, sem histórico fictício.

Pendências externas: nomes e RAs dos integrantes, commits e publicação no GitHub pela
equipe, upload dos vídeos e URLs na plataforma da disciplina. Confirmar requisitos
adicionais das disciplinas não presentes no texto recebido.
