package br.com.campanha.repository;

import br.com.campanha.model.Time;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeRepository extends JpaRepository<Time, Integer> {

    Optional<Time> findByNomeIgnoreCase(String nome);
}
