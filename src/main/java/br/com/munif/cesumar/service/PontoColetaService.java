package br.com.munif.cesumar.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.exception.ConflitoDoacaoException;
import br.com.munif.cesumar.exception.RecursoNotFoundException;
import br.com.munif.cesumar.mapper.PontoColetaMapper;
import br.com.munif.cesumar.model.PontoColeta;
import br.com.munif.cesumar.repository.DoacaoRepository;
import br.com.munif.cesumar.repository.PontoColetaRepository;

@Service
public class PontoColetaService {
    private final PontoColetaRepository repository;
    private final DoacaoRepository doacoes;
    private final PontoColetaMapper mapper;

    public PontoColetaService(PontoColetaRepository repository, DoacaoRepository doacoes, PontoColetaMapper mapper) {
        this.repository = repository;
        this.doacoes = doacoes;
        this.mapper = mapper;
    }

    public List<PontoColetaResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public PontoColetaResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModel(id));
    }

    public PontoColetaResponse criar(PontoColetaCreateRequest request) {
        return mapper.toResponse(repository.save(mapper.toModel(request)));
    }

    public PontoColetaResponse atualizar(String id, PontoColetaUpdateRequest request) {
        return mapper.toResponse(repository.save(mapper.updateModel(request, buscarModel(id))));
    }

    public void excluir(String id) {
        PontoColeta ponto = buscarModel(id);
        if (doacoes.existsByPontoColetaId(id)) {
            throw new ConflitoDoacaoException("O ponto possui doações vinculadas e não pode ser excluído.");
        }
        repository.delete(ponto);
    }

    private PontoColeta buscarModel(String id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNotFoundException("Ponto de coleta", id));
    }
}
