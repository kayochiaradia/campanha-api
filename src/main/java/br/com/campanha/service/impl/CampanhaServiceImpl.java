package br.com.campanha.service.impl;

import br.com.campanha.exception.RecursoNaoEncontradoException;
import br.com.campanha.model.Campanha;
import br.com.campanha.model.Time;
import br.com.campanha.repository.CampanhaRepository;
import br.com.campanha.repository.TimeRepository;
import br.com.campanha.service.CampanhaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Validated
public class CampanhaServiceImpl implements CampanhaService {

    private static final Logger LOGGER = LogManager.getLogger(CampanhaService.class);

    private static final int NUMERO_DIAS = 1;

    private final CampanhaRepository campanhaRepository;
    private final TimeRepository timeRepository;

    public CampanhaServiceImpl(CampanhaRepository campanhaRepository, TimeRepository timeRepository) {
        this.campanhaRepository = campanhaRepository;
        this.timeRepository = timeRepository;
    }

    @Override
    public List<Campanha> buscarTodasAsCampanhasAtivas(LocalDate hoje) {
        LOGGER.info("|Buscando Campanhas Ativas| com a data: {}", hoje);
        return campanhaRepository.findByFimVigenciaIsGreaterThanEqual(hoje);
    }

    @Override
    public List<Campanha> buscarCampanhasAtivasPorPeriodo(LocalDate inicioVigencia, LocalDate fimVigencia) throws NoSuchElementException {
        LOGGER.info("|Buscando Campanhas Ativas por Periodo| com as datas: Inicio Vigência: {}  - Data de Fim Vigência : {}", inicioVigencia, fimVigencia);
        return campanhaRepository.findByFimVigenciaBetween(inicioVigencia, fimVigencia);
    }

    @Override
    public Campanha cadastrarCampanha(String nomeDaCampanha, Integer idDoTimeCoracao, LocalDate inicioVigencia, LocalDate fimVigencia) {

        LOGGER.info("|Buscando Campanhas Ativas por Periodo| com as datas: Inicio Vigência: {}  - Data de Fim Vigência : {}", inicioVigencia, fimVigencia);
        List<Campanha> campanhas = campanhaRepository.findByFimVigenciaBetween(inicioVigencia, fimVigencia);
        campanhas.sort(Comparator.comparing(Campanha::getFimVigencia));

        campanhas.forEach(campanhaCadastrada -> {
            LOGGER.info("Aplicando a regra de adicionar mais um dia na Data: Data de Fim Vigência : {}", fimVigencia);
            campanhaCadastrada.setFimVigencia(campanhaCadastrada.getFimVigencia().plusDays(NUMERO_DIAS));
            adicionaDiaAoFimVigenciaRecursivo(campanhaCadastrada, campanhas);
        });

        campanhaRepository.saveAll(campanhas);
        Optional<Time> time = timeRepository.findById(idDoTimeCoracao);
        if (time.isPresent()) {
            LOGGER.info("Cadastrando Campanha com nome {} - ID time do coração: {}  -Data de Inicio Vigência: {}  - Data de Fim Vigência : {} ",
                    nomeDaCampanha, idDoTimeCoracao, inicioVigencia, fimVigencia);
            return campanhaRepository.save(new Campanha(nomeDaCampanha, inicioVigencia, fimVigencia, time.get()));
        }

        LOGGER.info("Não existe time cadastrado para o idDoTimeCoracao: {}", idDoTimeCoracao);
        throw new NoSuchElementException();
    }

    @Override
    public Optional<Campanha> buscarPorId(Integer id) {
        LOGGER.info("|Buscando Campanha Ativa por ID| com a ID: {}", id);
        return campanhaRepository.findById(id);
    }

    @Override
    public void deletarPorId(Integer id) {
        LOGGER.info("|Deletando Campanha Ativa por ID| com a ID: {}", id);
        campanhaRepository.deleteById(id);
    }

    @Override
    public void salvarCampanha(Campanha campanha) {
        LOGGER.info("|Salvando Campanha| campanha: {}", campanha);
        campanhaRepository.save(campanha);
    }

    @Override
    public List<Campanha> buscaPorTimeDoCoracao(String timeDoCoracao) throws RecursoNaoEncontradoException {
        LOGGER.info("|Buscando time por nome| com o nome: {}", timeDoCoracao);
        Optional<Time> time = timeRepository.findByNomeIgnoreCase(timeDoCoracao);
        if (time.isPresent()) {
            LOGGER.info("|Buscando Campanhas relacionadas ao time|");
            return campanhaRepository.
                    findByTime_IdAndInicioVigenciaIsLessThanEqualAndFimVigenciaIsGreaterThanEqual(time.get().getId(), LocalDate.now(), LocalDate.now());
        }
        LOGGER.info("Não encontrou time com o nome: {}", timeDoCoracao);
        throw new NoSuchElementException();
    }

    private void adicionaDiaAoFimVigenciaRecursivo(Campanha campanha, List<Campanha> campanhasCadastradas) {
        if (campanhasCadastradas.stream()
                .filter(campanhaCadastrada -> !campanhaCadastrada.equals(campanha))
                .anyMatch(campanhaCadastrada -> campanhaCadastrada.getFimVigencia().isEqual(campanha.getFimVigencia()))) {

            LOGGER.info("Adcionando fim de vigência na Campanha: {}", campanha);
            campanha.setFimVigencia(campanha.getFimVigencia().plusDays(NUMERO_DIAS));
            adicionaDiaAoFimVigenciaRecursivo(campanha, campanhasCadastradas);
        }
    }
}