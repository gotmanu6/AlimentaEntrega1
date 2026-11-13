package br.com.munif.cesumar.model;

import java.time.Instant;

public record EventoDoacao(StatusDoacao status, Instant instante) {
}
