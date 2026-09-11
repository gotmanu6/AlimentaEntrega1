# Alimenta — rede local de doação de alimentos

No computador atual, execute `.\iniciar.cmd` no PowerShell dentro desta pasta
para iniciar com a cópia local do Java 21 na pasta `.java21` (ignorada pelo Git).
Use `.\iniciar.cmd --verificar` para conferir Java e Maven. O iniciador configura
o Java somente durante sua execução. O MongoDB precisa estar ativo e a porta 8080 livre.

Prova de Conceito da **AEP 2026.2 — Engenharia de Software**, integrando Banco de Dados NoSQL, Paradigmas de Linguagem, Processo de Software e Projeto, Implementação e Testes.

## Problema, público e ODS

Pequenos doadores e pontos comunitários precisam registrar quais alimentos estão disponíveis, em que quantidade, até quando podem ser utilizados e onde serão recebidos. Informações dispersas dificultam a coordenação dessas doações.

O Alimenta permite que um operador cadastre pontos de coleta e lotes de alimentos. O público da PoC são associações de bairro, bancos de alimentos e pequenos mercados. Use dados fictícios na demonstração; não há cadastro de famílias.

A proposta se alinha ao **ODS 2 — Fome zero e agricultura sustentável**, especialmente à meta 2.1, ao apoiar a organização do acesso a alimentos por iniciativas comunitárias. É uma contribuição proposta, não uma comprovação de redução da fome. Fonte: [ONU Brasil — ODS 2](https://brasil.un.org/pt-br/sdgs/2).

## Primeira versão funcional

- CRUD de pontos de coleta com endereço aninhado.
- CRUD de doações com vários alimentos, quantidades, unidades e validade.
- Referência de cada doação a um ponto existente.
- Bloqueio de lotes vencidos e de exclusão de pontos com doações vinculadas.
- Interface web sem dependências externas, validação por campo e OpenAPI.

O exemplo original de linguagens permanece em `/crud.html` e `/api/linguagens`. Seu README está em [docs/projeto-base.md](docs/projeto-base.md). A PoC avaliada é o **Alimenta**.

## Tecnologias e organização

Java 21, Spring Boot 3.5.16, Spring Web, Spring Data MongoDB, Jakarta Validation, Springdoc, MongoDB 7.0, HTML/CSS/JavaScript nativos, Maven Wrapper, JUnit 5, Mockito, MockMvc, Testcontainers e JaCoCo.

O package `br.com.munif.cesumar` é organizado em Controller, DTO, Service, Mapper, Repository, Model, Exception e Configuration. Models de persistência não são contratos HTTP. Os testes ficam em `src/test`, a interface em `src/main/resources/static/alimenta.html` e a documentação em `docs`.

## Executar

Requisitos: **JDK 21 e Docker com Compose v2**. Configure `JAVA_HOME` para o JDK 21 e adicione seu `bin` ao `PATH`. Não é necessário instalar Maven.

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Linux/macOS: use `./mvnw` no lugar de `.\mvnw.cmd`.

Abra [Alimenta](http://localhost:8080/alimenta.html) e [Swagger UI](http://localhost:8080/docs). Cadastre um ponto na aba **Pontos de coleta**, volte a **Doações**, informe doador, ponto, validade e alimentos e salve.

A URI local padrão é `mongodb://root:Mongo@localhost:27018/linguagens?authSource=admin`. O nome do banco foi preservado; `pontos_coleta` e `doacoes` são coleções independentes. Para trocar a instância:

```powershell
$env:SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/alimenta"
.\mvnw.cmd spring-boot:run
```

Mongo Express: [localhost:18081](http://localhost:18081), usuário `cesumar`, senha `cesumar`. As credenciais do `.env` são didáticas. O volume `aepmongojava2026_mongo-data` persiste os dados. `docker compose down` encerra a infraestrutura preservando-os.

## Testes e cobertura reproduzível

```powershell
# Unitários e HTTP, sem MongoDB
.\mvnw.cmd clean test
# Suíte completa, MongoDB real isolado e cobertura mínima de 70%
.\mvnw.cmd clean verify
```

Abra `target/site/jacoco/index.html`. Os testes de integração criam seu próprio MongoDB com Testcontainers e não usam o banco de desenvolvimento. O JaCoCo mede Java; a configuração exclui bootstrap e configuração. A interface é verificada separadamente por testes HTTP e no navegador; o percentual Java não representa cobertura de JavaScript.

## Documentação e avaliação

- [Arquitetura e modelagem NoSQL](docs/alimenta-arquitetura.md)
- [Requisitos e evidências](docs/requisitos-aep.md)
- [Quadro de tarefas](docs/quadro-tarefas.md)
- [Comandos operacionais](HARNESS.md)

Equipe: nomes e RAs ainda não informados. Os commits e a publicação no GitHub serão feitos pela equipe, conforme orientação recebida. O remoto original identifica o projeto-base. Não há alegação de implantação pública ou impacto social medido.
