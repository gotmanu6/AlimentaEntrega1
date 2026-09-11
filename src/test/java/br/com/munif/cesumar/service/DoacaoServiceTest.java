package br.com.munif.cesumar.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.exception.*;
import br.com.munif.cesumar.mapper.DoacaoMapper;
import br.com.munif.cesumar.model.*;
import br.com.munif.cesumar.repository.*;

class DoacaoServiceTest {
    private final DoacaoRepository repository = mock(DoacaoRepository.class);
    private final PontoColetaRepository pontos = mock(PontoColetaRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-09T15:00:00Z"), ZoneOffset.UTC);
    private DoacaoService service;

    @BeforeEach
    void preparar() { service = new DoacaoService(repository, pontos, new DoacaoMapper(), clock); }

    @Test
    void deveAceitarValidadeNoDiaAtual() {
        when(pontos.existsById("p")).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        var resposta = service.criar(request(LocalDate.of(2026, 9, 9)));
        assertEquals("Mercado", resposta.doador());
        assertEquals(2, resposta.itens().getFirst().quantidade());
    }

    @Test
    void deveRejeitarValidadeAnteriorSemSalvar() {
        when(pontos.existsById("p")).thenReturn(true);
        assertThrows(ConflitoDoacaoException.class, () -> service.criar(request(LocalDate.of(2026, 9, 8))));
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarPontoAusenteSemSalvar() {
        assertThrows(RecursoNotFoundException.class, () -> service.criar(request(LocalDate.of(2026, 9, 10))));
        verifyNoInteractions(repository);
    }

    @Test
    void devePreservarIdNaAtualizacao() {
        when(repository.findById("d")).thenReturn(Optional.of(model()));
        when(pontos.existsById("p")).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        var r = request(LocalDate.of(2026, 9, 10));
        assertEquals("d", service.atualizar("d", new DoacaoUpdateRequest("p", "Outro", r.validade(), r.itens())).id());
    }

    private DoacaoCreateRequest request(LocalDate validade) {
        return new DoacaoCreateRequest("p", " Mercado ", validade, List.of(new ItemAlimentoRequest("Arroz", 2, "kg")));
    }

    private Doacao model() {
        return new Doacao("d", "p", "Mercado", LocalDate.of(2026, 9, 10), List.of(new ItemAlimento("Arroz", 2, "kg")));
    }
}
