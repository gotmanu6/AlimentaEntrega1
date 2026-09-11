package br.com.munif.cesumar.model;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doacoes")
public record Doacao(@Id String id, String pontoColetaId, String doador,
                     LocalDate validade, List<ItemAlimento> itens) {
}
