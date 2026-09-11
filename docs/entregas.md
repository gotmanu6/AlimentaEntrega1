# Entregas e evidências de testes

A primeira versão foi preservada antes da implementação de reserva e entrega.
Os códigos de cada etapa estão em `entregas/entrega1/codigo-fonte.zip` e
`entregas/entrega2/codigo-fonte.zip`. A equipe fará os commits e a publicação.

## Testes reproduzíveis

Com Java 21 e Docker ativos, execute `.\mvnw.cmd clean verify`.
O relatório fica em `target/site/jacoco/index.html` e exige pelo menos 70% das
linhas Java medidas. Bootstrap/configuração são excluídos; JavaScript não é medido.
Na segunda versão, execute também `node --test src/test/js/*.test.cjs`.
Não há dependência de Python para executar o projeto, os testes ou a cobertura.

## Vídeos sem áudio

- `entregas/entrega1/testes-entrega1.mp4`: saída real dos testes da primeira versão.
- `entregas/entrega2/testes-entrega2.mp4`: saída real dos testes Java e JavaScript da segunda versão.

Os vídeos exibem páginas dos logs de uma execução real, com os resultados e a
cobertura. Não são gravações contínuas da tela. Os logs completos ficam em
`entregas/entrega1/verificacao.log` e `entregas/entrega2/verificacao.log`.
Os vídeos narrados e seus arquivos de produção foram removidos a pedido da equipe.
Estes clipes mostram somente testes. A demonstração completa exigida pela atividade
(problema, ODS, arquitetura, fluxo e evolução, nos tempos previstos) ainda deve ser
preparada pela equipe.

## Publicação

Informar integrantes e RAs, publicar o código no repositório da equipe e identificar
cada marco por commit, tag ou release real. A pasta `entregas/` é ignorada pelo Git;
os vídeos devem ser enviados separadamente. Não há publicação automática.
