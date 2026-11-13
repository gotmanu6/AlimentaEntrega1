package br.com.munif.cesumar.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.munif.cesumar.model.Doacao;
import br.com.munif.cesumar.model.StatusDoacao;
import java.util.List;

public interface DoacaoRepository extends MongoRepository<Doacao, String> {
    boolean existsByPontoColetaId(String pontoColetaId);
    List<Doacao> findByStatus(StatusDoacao status);
}
