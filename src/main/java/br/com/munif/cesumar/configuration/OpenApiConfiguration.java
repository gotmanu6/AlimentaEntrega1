package br.com.munif.cesumar.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI linguagemOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Alimenta — API de Doações de Alimentos")
                        .description("PoC AEP 2026.2, ODS 2. Inclui o exemplo didático de linguagens de programação.")
                        .version("v1"));
    }
}
