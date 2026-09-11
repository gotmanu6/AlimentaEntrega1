package br.com.munif.cesumar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.munif.cesumar.model.Doacao;

public interface DoacaoRepository extends MongoRepository<Doacao, String> {
    boolean existsByPontoColetaId(String pontoColetaId);
}
