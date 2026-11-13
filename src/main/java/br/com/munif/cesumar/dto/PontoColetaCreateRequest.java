package br.com.munif.cesumar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PontoColetaCreateRequest(
        @NotBlank(message = "campo obrigatório") @Size(max = 100, message = "respeite o tamanho permitido") String nome,
        @NotNull(message = "campo obrigatório") @Valid EnderecoRequest endereco) {
}
