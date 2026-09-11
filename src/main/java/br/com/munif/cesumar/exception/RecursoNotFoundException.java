package br.com.munif.cesumar.exception;

public class RecursoNotFoundException extends RuntimeException {
    public RecursoNotFoundException(String recurso, String id) {
        super(recurso + " não encontrado: " + id);
    }
}
