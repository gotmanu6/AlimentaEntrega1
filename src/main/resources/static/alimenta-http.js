"use strict";

async function requisitarApi(
  caminho,
  method = "GET",
  body,
  transporte = fetch,
) {
  const resposta = await transporte(`/api/${caminho}`, {
    method,
    headers: { "Content-Type": "application/json" },
    ...(body === undefined ? {} : { body: JSON.stringify(body) }),
  });
  if (!resposta.ok) {
    let dados;
    try {
      dados = await resposta.json();
    } catch {
      dados = null;
    }
    const erro = new Error(
      dados?.message ||
        `Não foi possível concluir a operação (${resposta.status}).`,
    );
    erro.status = resposta.status;
    erro.fieldErrors = dados?.fieldErrors || {};
    throw erro;
  }
  return resposta.status === 204 ? null : resposta.json();
}

async function atualizarListaAposMutacao(carregar, notificar, texto) {
  try {
    await carregar();
    notificar(texto);
  } catch {
    notificar(
      `${texto} A atualização da lista falhou. Clique em Atualizar para consultar o resultado.`,
      "erro",
    );
  }
}

if (typeof module !== "undefined") {
  module.exports = { requisitarApi, atualizarListaAposMutacao };
}
