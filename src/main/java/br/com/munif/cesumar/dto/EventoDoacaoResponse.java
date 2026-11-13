package br.com.munif.cesumar.dto;

import java.time.Instant;

public record EventoDoacaoResponse(String status, Instant instante) {
}
