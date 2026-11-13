package br.com.munif.cesumar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ItemAlimentoRequest(
        @NotBlank(message = "campo obrigatório") @Size(max = 80, message = "respeite o tamanho permitido") String nome,
        @Positive(message = "a quantidade deve ser maior que zero") int quantidade,
        @NotBlank(message = "campo obrigatório") @Pattern(regexp = "kg|litro|unidade", message = "use kg, litro ou unidade") String unidade) {
}
