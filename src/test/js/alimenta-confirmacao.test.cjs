const { test } = require("node:test");
const assert = require("node:assert/strict");
const {
  confirmarAcao,
} = require("../../main/resources/static/alimenta-confirmacao.js");

function dialogoDeTeste() {
  const form = new EventTarget(),
    cancelar = new EventTarget(),
    texto = {};
  const botoes = [{ disabled: true }, { disabled: true }];
  const dialogo = new EventTarget();
  dialogo.querySelector = (seletor) =>
    ({ form, "[data-cancelar]": cancelar, p: texto })[seletor];
  dialogo.querySelectorAll = () => botoes;
  dialogo.showModal = () => {
    dialogo.open = true;
  };
  dialogo.close = () => {
    dialogo.open = false;
  };
  return { dialogo, form, cancelar, texto, botoes };
}

for (const acao of ["confirmar", "cancelar", "escape"]) {
  test(`${acao}: só executa a operação após confirmação explícita`, async () => {
    const ui = dialogoDeTeste();
    let executada = 0;
    const resposta = confirmarAcao(ui.dialogo, "Entregar o lote?").then(
      (confirmada) => {
        if (confirmada) executada++;
      },
    );
    await Promise.resolve();
    assert.equal(executada, 0);
    assert.equal(ui.dialogo.open, true);
    assert.equal(ui.texto.textContent, "Entregar o lote?");
    assert.ok(ui.botoes.every((b) => !b.disabled));
    const alvo =
      acao === "confirmar"
        ? ui.form
        : acao === "cancelar"
          ? ui.cancelar
          : ui.dialogo;
    const evento =
      acao === "confirmar"
        ? "submit"
        : acao === "cancelar"
          ? "click"
          : "cancel";
    alvo.dispatchEvent(new Event(evento, { cancelable: true }));
    await resposta;
    assert.equal(executada, acao === "confirmar" ? 1 : 0);
    assert.equal(ui.dialogo.open, false);
    alvo.dispatchEvent(new Event(evento));
    assert.equal(executada, acao === "confirmar" ? 1 : 0);
  });
}
