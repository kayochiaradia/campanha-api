package br.com.campanha.service;

import br.com.campanha.model.Time;
import br.com.campanha.vo.TimeVO;

import java.util.List;
import java.util.Optional;

public interface TimeService {

    Optional<Time> buscarPorId(Integer id);

    List<Time> buscarTodos();

    void deletarPorId(Integer id);

    Time salvarTimeTorcedor(TimeVO timeVO);
}
