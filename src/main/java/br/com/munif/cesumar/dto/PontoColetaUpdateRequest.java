package br.com.munif.cesumar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PontoColetaUpdateRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotNull @Valid EnderecoRequest endereco) {
}
