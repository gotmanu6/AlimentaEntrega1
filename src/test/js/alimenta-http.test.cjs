const { test } = require("node:test");
const assert = require("node:assert/strict");
const {
  requisitarApi,
  atualizarListaAposMutacao,
} = require("../../main/resources/static/alimenta-http.js");

test("envia contrato de criação como JSON para a API e devolve o documento", async () => {
  const entrada = {
    doador: "Mercado",
    itens: [{ nome: "Arroz", quantidade: 2, unidade: "kg" }],
  };
  const resultado = await requisitarApi(
    "doacoes",
    "POST",
    entrada,
    async (url, opcoes) => {
      assert.equal(url, "/api/doacoes");
      assert.equal(opcoes.method, "POST");
      assert.deepEqual(JSON.parse(opcoes.body), entrada);
      return {
        ok: true,
        status: 201,
        json: async () => ({ id: "d", ...entrada }),
      };
    },
  );
  assert.equal(resultado.id, "d");
});

test("consulta sem enviar corpo e preserva o filtro", async () => {
  await requisitarApi(
    "doacoes?status=RESERVADA",
    undefined,
    undefined,
    async (url, opcoes) => {
      assert.equal(url, "/api/doacoes?status=RESERVADA");
      assert.equal(opcoes.method, "GET");
      assert.equal(opcoes.body, undefined);
      return { ok: true, status: 200, json: async () => [] };
    },
  );
});

test("exclusão 204 não tenta interpretar um corpo inexistente", async () => {
  assert.equal(
    await requisitarApi("doacoes/d", "DELETE", undefined, async () => ({
      ok: true,
      status: 204,
      json: () => {
        throw new Error("não chamar");
      },
    })),
    null,
  );
});

test("preserva os erros de campos aninhados e o status HTTP", async () => {
  await assert.rejects(
    requisitarApi("doacoes", "POST", {}, async () => ({
      ok: false,
      status: 400,
      json: async () => ({
        message: "Dados inválidos",
        fieldErrors: { "itens[0].quantidade": "Deve ser positiva" },
      }),
    })),
    (erro) =>
      erro.status === 400 &&
      erro.fieldErrors["itens[0].quantidade"] === "Deve ser positiva",
  );
});

test("mantém o conflito de reserva para apresentação ao usuário", async () => {
  await assert.rejects(
    requisitarApi("doacoes/d/reserva", "POST", {}, async () => ({
      ok: false,
      status: 409,
      json: async () => ({ message: "Lote já reservado" }),
    })),
    /Lote já reservado/,
  );
});

test("resposta de erro sem JSON continua produzindo mensagem legível", async () => {
  for (const json of [
    async () => {
      throw new SyntaxError("HTML");
    },
    async () => null,
  ]) {
    await assert.rejects(
      requisitarApi("doacoes", "GET", undefined, async () => ({
        ok: false,
        status: 503,
        json,
      })),
      /503/,
    );
  }
});

test("erro de conexão é propagado", async () => {
  await assert.rejects(
    requisitarApi("doacoes", "GET", undefined, async () => {
      throw new Error("Sem conexão");
    }),
    /Sem conexão/,
  );
});

test("falha de atualização não esconde que a gravação já aconteceu", async () => {
  const mensagens = [];
  await atualizarListaAposMutacao(
    async () => {
      throw new Error("Sem rede");
    },
    (...args) => mensagens.push(args),
    "Doação salva.",
  );
  assert.match(mensagens[0][0], /^Doação salva\./);
  assert.match(mensagens[0][0], /atualização da lista falhou/);
  assert.equal(mensagens[0][1], "erro");
});

test("lista atualizada confirma sucesso", async () => {
  const mensagens = [];
  await atualizarListaAposMutacao(
    async () => {},
    (...args) => mensagens.push(args),
    "Reserva registrada.",
  );
  assert.deepEqual(mensagens, [["Reserva registrada."]]);
});
