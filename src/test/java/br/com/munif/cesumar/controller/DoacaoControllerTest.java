package br.com.munif.cesumar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import br.com.munif.cesumar.service.DoacaoService;

@WebMvcTest(DoacaoController.class)
class DoacaoControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private DoacaoService service;

    @Test
    void deveTraduzirConcorrenciaPara409ComErroPadronizado() throws Exception {
        when(service.reservar(eq("d"), any())).thenThrow(new OptimisticLockingFailureException("versão mudou"));
        mvc.perform(post("/api/doacoes/d/reserva").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"beneficiario\":\"Associação\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/api/doacoes/d/reserva"))
                .andExpect(jsonPath("$.message").value("A doação foi alterada por outra operação. Atualize a lista."))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }
}
