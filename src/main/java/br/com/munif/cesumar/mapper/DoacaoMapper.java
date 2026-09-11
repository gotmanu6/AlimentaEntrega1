package br.com.munif.cesumar.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.model.Doacao;
import br.com.munif.cesumar.model.ItemAlimento;

@Component
public class DoacaoMapper {
    public Doacao toModel(DoacaoCreateRequest request) {
        return new Doacao(null, request.pontoColetaId(), request.doador().trim(), request.validade(), itens(request.itens()));
    }

    public Doacao updateModel(DoacaoUpdateRequest request, Doacao atual) {
        return new Doacao(atual.id(), request.pontoColetaId(), request.doador().trim(), request.validade(), itens(request.itens()));
    }

    private List<ItemAlimento> itens(List<ItemAlimentoRequest> requests) {
        return requests.stream().map(item -> new ItemAlimento(item.nome().trim(), item.quantidade(), item.unidade())).toList();
    }

    public DoacaoResponse toResponse(Doacao model) {
        return new DoacaoResponse(model.id(), model.pontoColetaId(), model.doador(), model.validade(),
                model.itens().stream().map(item -> new ItemAlimentoResponse(item.nome(), item.quantidade(), item.unidade())).toList());
    }
}
