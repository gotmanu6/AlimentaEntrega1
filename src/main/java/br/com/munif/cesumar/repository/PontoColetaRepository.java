package br.com.munif.cesumar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.munif.cesumar.model.PontoColeta;

public interface PontoColetaRepository extends MongoRepository<PontoColeta, String> {
}
