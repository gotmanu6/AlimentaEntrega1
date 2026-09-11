package br.com.munif.cesumar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ItemAlimentoRequest(
        @NotBlank @Size(max = 80) String nome,
        @Positive(message = "a quantidade deve ser maior que zero") int quantidade,
        @NotBlank @Pattern(regexp = "kg|litro|unidade", message = "use kg, litro ou unidade") String unidade) {
}
