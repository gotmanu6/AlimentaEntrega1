package br.com.munif.cesumar.dto;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DoacaoCreateRequest(
        @NotBlank String pontoColetaId,
        @NotBlank @Size(max = 100) String doador,
        @NotNull LocalDate validade,
        @NotNull @Size(min = 1, max = 20) List<@NotNull @Valid ItemAlimentoRequest> itens) {
}
