package br.com.munif.cesumar.dto;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DoacaoCreateRequest(
        @NotBlank(message = "campo obrigatório") String pontoColetaId,
        @NotBlank(message = "campo obrigatório") @Size(max = 100, message = "respeite o tamanho permitido") String doador,
        @NotNull(message = "campo obrigatório") LocalDate validade,
        @NotNull(message = "campo obrigatório") @Size(min = 1, max = 20, message = "respeite o tamanho permitido") List<@NotNull(message = "campo obrigatório") @Valid ItemAlimentoRequest> itens) {
}
