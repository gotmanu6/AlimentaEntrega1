package br.com.munif.cesumar.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.exception.ConflitoDoacaoException;
import br.com.munif.cesumar.exception.RecursoNotFoundException;
import br.com.munif.cesumar.mapper.DoacaoMapper;
import br.com.munif.cesumar.model.Doacao;
import br.com.munif.cesumar.repository.DoacaoRepository;
import br.com.munif.cesumar.repository.PontoColetaRepository;

@Service
public class DoacaoService {
    private final DoacaoRepository repository;
    private final PontoColetaRepository pontos;
    private final DoacaoMapper mapper;
    private final Clock clock;

    public DoacaoService(DoacaoRepository repository, PontoColetaRepository pontos, DoacaoMapper mapper, Clock clock) {
        this.repository = repository;
        this.pontos = pontos;
        this.mapper = mapper;
        this.clock = clock;
    }

    public List<DoacaoResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public DoacaoResponse buscarPorId(String id) {
        return mapper.toResponse(buscarModel(id));
    }

    public DoacaoResponse criar(DoacaoCreateRequest request) {
        validarReferenciaEValidade(request.pontoColetaId(), request.validade());
        return mapper.toResponse(repository.save(mapper.toModel(request)));
    }

    public DoacaoResponse atualizar(String id, DoacaoUpdateRequest request) {
        Doacao atual = buscarModel(id);
        validarReferenciaEValidade(request.pontoColetaId(), request.validade());
        return mapper.toResponse(repository.save(mapper.updateModel(request, atual)));
    }

    public void excluir(String id) {
        repository.delete(buscarModel(id));
    }

    private Doacao buscarModel(String id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNotFoundException("Doação", id));
    }

    private void validarReferenciaEValidade(String pontoId, LocalDate validade) {
        if (!pontos.existsById(pontoId)) {
            throw new RecursoNotFoundException("Ponto de coleta", pontoId);
        }
        if (validade.isBefore(LocalDate.now(clock))) {
            throw new ConflitoDoacaoException("Não é permitido oferecer alimentos vencidos.");
        }
    }
}
