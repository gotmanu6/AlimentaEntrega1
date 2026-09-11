package br.com.munif.cesumar.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.model.*;
import br.com.munif.cesumar.repository.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlimentaApiIT {
    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongo(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private PontoColetaRepository pontos;
    @Autowired private DoacaoRepository doacoes;

    @BeforeEach
    void limpar() { doacoes.deleteAll(); pontos.deleteAll(); }

    @Test
    void deveExecutarCrudDoPontoComEnderecoPersistido() throws Exception {
        var request = new PontoColetaCreateRequest(" Centro comunitário ", endereco());
        var resposta = mvc.perform(post("/api/pontos-coleta").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Centro comunitário"))
                .andReturn().getResponse();
        String id = json.readTree(resposta.getContentAsString()).get("id").asText();
        assertEquals("Maringá", pontos.findById(id).orElseThrow().endereco().cidade());
        mvc.perform(get("/api/pontos-coleta/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.rua").value("Rua A, 10"));
        mvc.perform(get("/api/pontos-coleta")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/pontos-coleta/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new PontoColetaUpdateRequest("Novo nome", endereco()))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        assertEquals("Novo nome", pontos.findById(id).orElseThrow().nome());
        mvc.perform(delete("/api/pontos-coleta/" + id)).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertFalse(pontos.existsById(id));
    }

    @Test
    void deveExecutarCrudDaDoacaoComReferenciaEListaDeSubdocumentos() throws Exception {
        String pontoId = ponto().id();
        var response = mvc.perform(post("/api/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(doacao(pontoId))))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andReturn().getResponse();
        String id = json.readTree(response.getContentAsString()).get("id").asText();
        Doacao persistida = doacoes.findById(id).orElseThrow();
        assertEquals(pontoId, persistida.pontoColetaId());
        assertEquals(2, persistida.itens().size());
        assertEquals("Arroz", persistida.itens().getFirst().nome());
        mvc.perform(get("/api/doacoes/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].quantidade").value(5));
        mvc.perform(get("/api/doacoes")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/doacoes/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new DoacaoUpdateRequest(pontoId, "Doador atualizado",
                                LocalDate.now().plusDays(20), doacao(pontoId).itens()))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        assertEquals("Doador atualizado", doacoes.findById(id).orElseThrow().doador());
        mvc.perform(delete("/api/pontos-coleta/" + pontoId)).andExpect(status().isConflict());
        mvc.perform(delete("/api/doacoes/" + id)).andExpect(status().isNoContent());
        assertEquals(0, doacoes.count());
    }

    @Test
    void deveRejeitarReferenciaInexistenteEAlimentoVencido() throws Exception {
        mvc.perform(post("/api/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(doacao("ausente")))).andExpect(status().isNotFound());
        var vencida = new DoacaoCreateRequest(ponto().id(), "Mercado", LocalDate.now().minusDays(2), doacao("x").itens());
        mvc.perform(post("/api/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(vencida))).andExpect(status().isConflict());
        assertEquals(0, doacoes.count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void deveRetornar404ParaRecursosAusentes(String metodo) throws Exception {
        for (String path : List.of("/api/pontos-coleta/ausente", "/api/doacoes/ausente")) {
            Object request = path.contains("pontos") ? new PontoColetaUpdateRequest("Ponto", endereco()) : doacao("x");
            mvc.perform(request(HttpMethod.valueOf(metodo), path).contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(request)))
                    .andExpect(status().isNotFound()).andExpect(jsonPath("$.path").value(path));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT"})
    void deveValidarSubdocumentosSemPersistir(String metodo) throws Exception {
        String path = "/api/doacoes" + (metodo.equals("PUT") ? "/ausente" : "");
        mvc.perform(request(HttpMethod.valueOf(metodo), path).contentType(MediaType.APPLICATION_JSON).content("""
                        {"pontoColetaId":"p","doador":"Doador","validade":"2099-01-01",
                         "itens":[{"nome":"","quantidade":0,"unidade":"inválida"}]}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors['itens[0].nome']").exists())
                .andExpect(jsonPath("$.fieldErrors['itens[0].quantidade']").exists())
                .andExpect(jsonPath("$.fieldErrors['itens[0].unidade']").exists());
        mvc.perform(request(HttpMethod.valueOf(metodo), "/api/pontos-coleta" + (metodo.equals("PUT") ? "/x" : ""))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome":"Ponto","endereco":{"rua":"","bairro":"Centro","cidade":"Maringá","estado":"Paraná"}}
                                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors['endereco.rua']").exists());
        assertEquals(0, doacoes.count());
        assertEquals(0, pontos.count());
    }

    @Test
    void deveExporContratosDaPocEPagina() throws Exception {
        mvc.perform(get("/alimenta.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/doacoes'].post.responses['201']").exists())
                .andExpect(jsonPath("$.components.schemas.DoacaoCreateRequest.properties.itens").exists());
    }

    private PontoColeta ponto() {
        return pontos.save(new PontoColeta(null, "Centro", new Endereco("Rua A, 10", "Centro", "Maringá", "PR")));
    }

    private EnderecoRequest endereco() { return new EnderecoRequest("Rua A, 10", "Centro", "Maringá", "PR"); }

    private DoacaoCreateRequest doacao(String pontoId) {
        return new DoacaoCreateRequest(pontoId, "Mercado exemplo", LocalDate.now().plusDays(30),
                List.of(new ItemAlimentoRequest("Arroz", 5, "kg"), new ItemAlimentoRequest("Feijão", 3, "kg")));
    }
}
