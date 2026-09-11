package br.com.munif.cesumar.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.munif.cesumar.model.Linguagem;
import br.com.munif.cesumar.repository.LinguagemRepository;

@ExtendWith(MockitoExtension.class)
class DevelopmentDataConfigurationTest {

    @Mock
    private LinguagemRepository repository;

    @Test
    void deveCompletarCargaSemDuplicarNemSobrescreverRegistrosExistentes() throws Exception {
        Map<String, Linguagem> documentos = new HashMap<>();
        Linguagem existente = new Linguagem("java", "Java editado", LocalDate.of(1995, 5, 23), "Autor editado");
        documentos.put(existente.getId(), existente);
        when(repository.existsById(anyString())).thenAnswer(invocacao -> documentos.containsKey(invocacao.getArgument(0)));
        when(repository.save(any(Linguagem.class))).thenAnswer(invocacao -> {
            Linguagem linguagem = invocacao.getArgument(0);
            documentos.put(linguagem.getId(), linguagem);
            return linguagem;
        });

        var carga = new DevelopmentDataConfiguration().loadDevelopmentData(repository);
        carga.run();
        carga.run();

        assertEquals(Set.of("java", "python", "c", "rust", "javascript"), documentos.keySet());
        assertSame(existente, documentos.get("java"));
        assertEquals("Java editado", documentos.get("java").getNome());
        assertEquals("Autor editado", documentos.get("java").getAutor());
        assertEquals("Python", documentos.get("python").getNome());
    }
}
