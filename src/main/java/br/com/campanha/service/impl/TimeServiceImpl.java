package br.com.campanha.service.impl;

import br.com.campanha.model.Time;
import br.com.campanha.repository.TimeRepository;
import br.com.campanha.service.TimeService;
import br.com.campanha.vo.TimeVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class TimeServiceImpl implements TimeService {

    private static final Logger LOGGER = LogManager.getLogger(TimeService.class);

    private final TimeRepository timeRepository;

    public TimeServiceImpl(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    @Override
    public Optional<Time> buscarPorId(Integer id) {
        LOGGER.info("|Buscando time por ID| com o ID: {}", id);
        return timeRepository.findById(id);
    }

    @Override
    public List<Time> buscarTodos() {
        LOGGER.info("|Buscando todos os times|");
        return timeRepository.findAll();
    }

    @Override
    public void deletarPorId(Integer id) {
        LOGGER.info("|Deletando o time por ID| com o ID: {}", id);
        timeRepository.deleteById(id);
    }

    @Override
    public Time salvarTimeTorcedor(TimeVO timeVO) {
        LOGGER.info("|Salvar time| com o time: {}", timeVO);
        return timeRepository.save(new Time(timeVO));
    }
}
