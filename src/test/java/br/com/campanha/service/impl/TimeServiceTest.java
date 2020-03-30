package br.com.campanha.service.impl;

import br.com.campanha.base.IntegrationBaseTest;
import br.com.campanha.model.Time;
import br.com.campanha.service.TimeService;
import br.com.campanha.vo.TimeVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeServiceTest extends IntegrationBaseTest {

    @Autowired
    private TimeService timeService;

    @Test
    public void timeDeveSerCadastrado(){
        Time time = timeService.salvarTimeTorcedor(new TimeVO("Alagoinha", null));

        assertThat(time)
                .as("O time deve ser cadastrado na base de dados")
                .isNotNull()
                .extracting("id", "nome")
                .contains(5, "Alagoinha");

    }

    @Test
    public void buscarTodosOsTimesCadastrados(){

        assertThat(timeService.buscarTodos())
                .as("Deve trazer todos os times cadastrados")
                .hasSize(3);
    }

    @Test
    public void deletarUmTimeCadastrado() {
        timeService.deletarPorId(4);

        assertThat(timeService.buscarPorId(4))
                .as("O resultado deve ser vazio, pois, o time foi deletado")
                .isEmpty();
    }
}
