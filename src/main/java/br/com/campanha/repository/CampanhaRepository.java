package br.com.campanha.repository;

import br.com.campanha.model.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, Integer> {

    List<Campanha> findByFimVigenciaIsGreaterThanEqual(LocalDate hoje);

    List<Campanha> findByFimVigenciaBetween(LocalDate inicioVigencia, LocalDate fimVigencia);

    List<Campanha> findByTime_IdAndInicioVigenciaIsLessThanEqualAndFimVigenciaIsGreaterThanEqual(Integer timeCoracaoId, LocalDate inicioVigencia, LocalDate fimVigencia);
}