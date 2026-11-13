package br.com.munif.cesumar.mapper;

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.model.Doacao;
import br.com.munif.cesumar.model.ItemAlimento;
import br.com.munif.cesumar.model.StatusDoacao;
import br.com.munif.cesumar.model.EventoDoacao;

@Component
public class DoacaoMapper {
    public Doacao toModel(DoacaoCreateRequest request, Instant instante) {
        return new Doacao(null, request.pontoColetaId(), request.doador().trim(), request.validade(), itens(request.itens()),
                StatusDoacao.DISPONIVEL, null, List.of(new EventoDoacao(StatusDoacao.DISPONIVEL, instante)), null);
    }

    public Doacao updateModel(DoacaoUpdateRequest request, Doacao atual) {
        return new Doacao(atual.id(), request.pontoColetaId(), request.doador().trim(), request.validade(), itens(request.itens()),
                atual.status(), atual.beneficiario(), atual.historico(), atual.versao());
    }

    private List<ItemAlimento> itens(List<ItemAlimentoRequest> requests) {
        return requests.stream().map(item -> new ItemAlimento(item.nome().trim(), item.quantidade(), item.unidade())).toList();
    }

    public Doacao comSituacao(Doacao atual, StatusDoacao status, String beneficiario, Instant instante) {
        List<EventoDoacao> historico = new ArrayList<>(atual.historico());
        historico.add(new EventoDoacao(status, instante));
        return new Doacao(atual.id(), atual.pontoColetaId(), atual.doador(), atual.validade(), atual.itens(),
                status, beneficiario, List.copyOf(historico), atual.versao());
    }

    public DoacaoResponse toResponse(Doacao model, LocalDate hoje) {
        return new DoacaoResponse(model.id(), model.pontoColetaId(), model.doador(), model.validade(),
                model.itens().stream().map(item -> new ItemAlimentoResponse(item.nome(), item.quantidade(), item.unidade())).toList(),
                model.status().name(), model.beneficiario(),
                model.historico().stream().map(e -> new EventoDoacaoResponse(e.status().name(), e.instante())).toList(),
                model.status() != StatusDoacao.ENTREGUE && model.validade().isBefore(hoje));
    }
}
