package br.com.munif.cesumar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pontos_coleta")
public record PontoColeta(@Id String id, String nome, Endereco endereco) {
}
