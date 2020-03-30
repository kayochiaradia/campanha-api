package br.com.campanha.service;

import br.com.campanha.exception.RecursoNaoEncontradoException;
import br.com.campanha.model.Campanha;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface CampanhaService {

    List<Campanha> buscarTodasAsCampanhasAtivas(LocalDate hoje);

    List<Campanha> buscarCampanhasAtivasPorPeriodo(LocalDate inicioVigencia, LocalDate fimVigencia);

    Campanha cadastrarCampanha(@NotNull String nomeDaCampanha, @NotNull Integer idDoTimeCoracao,
                               @NotNull LocalDate inicioVigencia, @NotNull LocalDate fimVigencia) throws NoSuchElementException;

    Optional<Campanha> buscarPorId(Integer id);

    void deletarPorId(Integer id);

    void salvarCampanha(Campanha campanha);

    List<Campanha> buscaPorTimeDoCoracao(String timeDoCoracao) throws RecursoNaoEncontradoException;
}