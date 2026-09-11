package br.com.munif.cesumar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank @Size(max = 150) String rua,
        @NotBlank @Size(max = 80) String bairro,
        @NotBlank @Size(max = 80) String cidade,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "use a sigla da UF com duas letras maiúsculas") String estado) {
}
