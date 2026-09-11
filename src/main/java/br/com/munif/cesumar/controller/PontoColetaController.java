package br.com.munif.cesumar.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.service.PontoColetaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pontos-coleta")
public class PontoColetaController {
    private final PontoColetaService service;

    public PontoColetaController(PontoColetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PontoColetaResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PontoColetaResponse buscarPorId(@PathVariable String id) { return service.buscarPorId(id); }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Ponto de coleta criado")
    public ResponseEntity<PontoColetaResponse> criar(@Valid @RequestBody PontoColetaCreateRequest request) {
        PontoColetaResponse response = service.criar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    public PontoColetaResponse atualizar(@PathVariable String id, @Valid @RequestBody PontoColetaUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Ponto de coleta excluído")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
