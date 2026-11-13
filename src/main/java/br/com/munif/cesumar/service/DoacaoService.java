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
import br.com.munif.cesumar.model.StatusDoacao;
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

    public List<DoacaoResponse> listar(StatusDoacao status) {
        List<Doacao> doacoes = status == null ? repository.findAll() : repository.findByStatus(status);
        return doacoes.stream().map(this::resposta).toList();
    }

    public DoacaoResponse buscarPorId(String id) {
        return resposta(buscarModel(id));
    }

    public DoacaoResponse criar(DoacaoCreateRequest request) {
        validarReferenciaEValidade(request.pontoColetaId(), request.validade());
        return resposta(repository.save(mapper.toModel(request, clock.instant())));
    }

    public DoacaoResponse atualizar(String id, DoacaoUpdateRequest request) {
        Doacao atual = buscarModel(id);
        exigirSituacao(atual, StatusDoacao.DISPONIVEL);
        validarReferenciaEValidade(request.pontoColetaId(), request.validade());
        return resposta(repository.save(mapper.updateModel(request, atual)));
    }

    public void excluir(String id) {
        Doacao atual = buscarModel(id);
        exigirSituacao(atual, StatusDoacao.DISPONIVEL);
        repository.delete(atual);
    }

    public DoacaoResponse reservar(String id, ReservaDoacaoRequest request) {
        Doacao atual = buscarModel(id);
        exigirSituacao(atual, StatusDoacao.DISPONIVEL);
        validarValidade(atual.validade());
        return resposta(repository.save(mapper.comSituacao(atual, StatusDoacao.RESERVADA,
                request.beneficiario().trim(), clock.instant())));
    }

    public DoacaoResponse entregar(String id) {
        Doacao atual = buscarModel(id);
        exigirSituacao(atual, StatusDoacao.RESERVADA);
        validarValidade(atual.validade());
        return resposta(repository.save(mapper.comSituacao(atual, StatusDoacao.ENTREGUE,
                atual.beneficiario(), clock.instant())));
    }

    public DoacaoResumoResponse resumo() {
        List<DoacaoResponse> doacoes = listar(null);
        long disponiveis = doacoes.stream().filter(d -> d.status().equals("DISPONIVEL") && !d.vencida()).count();
        long reservadas = doacoes.stream().filter(d -> d.status().equals("RESERVADA")).count();
        long entregues = doacoes.stream().filter(d -> d.status().equals("ENTREGUE")).count();
        long vencidas = doacoes.stream().filter(DoacaoResponse::vencida).count();
        return new DoacaoResumoResponse(disponiveis, reservadas, entregues, vencidas);
    }

    private DoacaoResponse resposta(Doacao model) {
        return mapper.toResponse(model, LocalDate.now(clock));
    }

    private void exigirSituacao(Doacao doacao, StatusDoacao esperada) {
        if (doacao.status() != esperada) {
            throw new ConflitoDoacaoException("Operação exige doação " + esperada + "; situação atual: " + doacao.status() + ".");
        }
    }

    private Doacao buscarModel(String id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNotFoundException("Doação", id));
    }

    private void validarReferenciaEValidade(String pontoId, LocalDate validade) {
        if (!pontos.existsById(pontoId)) {
            throw new RecursoNotFoundException("Ponto de coleta", pontoId);
        }
        validarValidade(validade);
    }

    private void validarValidade(LocalDate validade) {
        if (validade.isBefore(LocalDate.now(clock))) {
            throw new ConflitoDoacaoException("Não é permitido oferecer alimentos vencidos.");
        }
    }
}
