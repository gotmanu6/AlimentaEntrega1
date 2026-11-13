package br.com.munif.cesumar.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.com.munif.cesumar.dto.*;
import br.com.munif.cesumar.service.DoacaoService;
import br.com.munif.cesumar.model.StatusDoacao;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doacoes")
public class DoacaoController {
    private final DoacaoService service;

    public DoacaoController(DoacaoService service) { this.service = service; }

    @GetMapping
    public List<DoacaoResponse> listar(@RequestParam(required = false) StatusDoacao status) { return service.listar(status); }

    @GetMapping("/resumo")
    public DoacaoResumoResponse resumo() { return service.resumo(); }

    @PostMapping("/{id}/reserva")
    public DoacaoResponse reservar(@PathVariable String id, @Valid @RequestBody ReservaDoacaoRequest request) {
        return service.reservar(id, request);
    }

    @PostMapping("/{id}/entrega")
    public DoacaoResponse entregar(@PathVariable String id) { return service.entregar(id); }

    @GetMapping("/{id}")
    public DoacaoResponse buscarPorId(@PathVariable String id) { return service.buscarPorId(id); }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Doação criada")
    public ResponseEntity<DoacaoResponse> criar(@Valid @RequestBody DoacaoCreateRequest request) {
        DoacaoResponse response = service.criar(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    public DoacaoResponse atualizar(@PathVariable String id, @Valid @RequestBody DoacaoUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Doação excluída")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
