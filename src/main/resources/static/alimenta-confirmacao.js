"use strict";

function confirmarAcao(dialogo, texto) {
  const form = dialogo.querySelector("form");
  const cancelar = dialogo.querySelector("[data-cancelar]");
  dialogo.querySelector("p").textContent = texto;
  return new Promise((resolve) => {
    function concluir(confirmada) {
      form.removeEventListener("submit", confirmar);
      cancelar.removeEventListener("click", recusar);
      dialogo.removeEventListener("cancel", recusar);
      dialogo.close();
      resolve(confirmada);
    }
    function confirmar(evento) {
      evento.preventDefault();
      concluir(true);
    }
    function recusar(evento) {
      evento.preventDefault();
      concluir(false);
    }
    form.addEventListener("submit", confirmar);
    cancelar.addEventListener("click", recusar);
    dialogo.addEventListener("cancel", recusar);
    dialogo.querySelectorAll("button").forEach((b) => (b.disabled = false));
    dialogo.showModal();
  });
}

if (typeof module !== "undefined") module.exports = { confirmarAcao };
