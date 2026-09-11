package br.com.munif.cesumar.mapper;

import org.springframework.stereotype.Component;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.model.Endereco;
import br.com.munif.cesumar.model.PontoColeta;

@Component
public class PontoColetaMapper {
    public PontoColeta toModel(PontoColetaCreateRequest request) {
        return new PontoColeta(null, request.nome().trim(), endereco(request.endereco()));
    }

    public PontoColeta updateModel(PontoColetaUpdateRequest request, PontoColeta atual) {
        return new PontoColeta(atual.id(), request.nome().trim(), endereco(request.endereco()));
    }

    private Endereco endereco(EnderecoRequest request) {
        return new Endereco(request.rua().trim(), request.bairro().trim(), request.cidade().trim(), request.estado());
    }

    public PontoColetaResponse toResponse(PontoColeta model) {
        Endereco e = model.endereco();
        return new PontoColetaResponse(model.id(), model.nome(),
                new EnderecoResponse(e.rua(), e.bairro(), e.cidade(), e.estado()));
    }
}
