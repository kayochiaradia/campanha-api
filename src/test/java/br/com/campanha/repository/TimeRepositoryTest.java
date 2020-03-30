package br.com.campanha.repository;

import br.com.campanha.base.IntegrationBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeRepositoryTest extends IntegrationBaseTest {

    @Autowired
    private TimeRepository timeRepository;

    @Test
    public void findByTimeIdTest() {
        assertThat(timeRepository.findByNomeIgnoreCase("Corinthians")).isNotEmpty();
    }

    @Test
    public void naoDeveTrazerNenhumTime() {

        assertThat(timeRepository.findByNomeIgnoreCase("Guarani"))
                .as("Nenhum time deve ser retornada pois não existem nenhum time cadastrado.").isEmpty();
    }
}
