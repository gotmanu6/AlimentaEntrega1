package br.com.munif.cesumar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReservaDoacaoRequest(
        @NotBlank(message = "informe a organização beneficiária") @Size(max = 100, message = "respeite o tamanho permitido") String beneficiario) {
}
