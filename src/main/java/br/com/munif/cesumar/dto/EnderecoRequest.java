package br.com.munif.cesumar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank(message = "campo obrigatório") @Size(max = 150, message = "respeite o tamanho permitido") String rua,
        @NotBlank(message = "campo obrigatório") @Size(max = 80, message = "respeite o tamanho permitido") String bairro,
        @NotBlank(message = "campo obrigatório") @Size(max = 80, message = "respeite o tamanho permitido") String cidade,
        @NotBlank(message = "campo obrigatório") @Pattern(regexp = "[A-Z]{2}", message = "use a sigla da UF com duas letras maiúsculas") String estado) {
}
