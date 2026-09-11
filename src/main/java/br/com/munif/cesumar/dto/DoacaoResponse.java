package br.com.munif.cesumar.dto;

import java.time.LocalDate;
import java.util.List;

public record DoacaoResponse(String id, String pontoColetaId, String doador,
                            LocalDate validade, List<ItemAlimentoResponse> itens) {
}
