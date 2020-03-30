package br.com.campanha.service.impl;


import br.com.campanha.base.IntegrationBaseTest;
import br.com.campanha.model.Campanha;
import br.com.campanha.repository.CampanhaRepository;
import br.com.campanha.service.CampanhaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


public class CampanhaServiceTest extends IntegrationBaseTest {

    @Autowired
    private CampanhaService campanhaService;

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Test
    public void campanhaDeveSerCadastradasComDadosCorretos(){
        Campanha campanha = campanhaService.cadastrarCampanha("Campanha 3", 2,
                LocalDate.now(), LocalDate.now().plusDays(5));

        assertThat(campanha)
                .as("A camapanha 3 deve ser cadastrada na base de dados e ter os dados corretos conforme os parametros")
                .isNotNull()
                .extracting("nome", "time.id", "inicioVigencia", "fimVigencia")
                .contains("Campanha 3", 2, LocalDate.now(), LocalDate.now().plusDays(5));


        assertThat(campanhaRepository.findAll())
                .as("As camapanhas 1 e 2 devem ter seus dados atualizados conforme regras de data de fim vigência")
                .extracting("nome", "time.id", "inicioVigencia", "fimVigencia")
                .contains(tuple("Nova Campanha", 1, LocalDate.now(), LocalDate.now().plusDays(7)),
                        tuple("Outra Campanha", 2, LocalDate.now(), LocalDate.now().plusDays(6)),
                        tuple("Campanha 3", 2, LocalDate.now(), LocalDate.now().plusDays(5)));

    }

    @Test
    public void buscarCampanhasAtivasPorPeriodo(){

        assertThat(campanhaService.buscarCampanhasAtivasPorPeriodo(
                LocalDate.now(), LocalDate.now().plusDays(6)))
                .as("Deve trazer as tês Campanhas ativas dado que todas estão com a vigência dentro deste período")
                .hasSize(3);

        assertThat(campanhaService.buscarCampanhasAtivasPorPeriodo(
                LocalDate.now(), LocalDate.now().plusDays(7)))
                .as("Deve trazer as quatro Campanhas ativas dado que todas estão com a vigência dentro deste período")
                .hasSize(4);

        assertThat(campanhaService.buscarCampanhasAtivasPorPeriodo(
                LocalDate.now(), LocalDate.now().plusDays(5)))
                .as("Deve trazersomente uma Campanha (Campanha 3) dado que somente ela esta dentro da vigência deste período")
                .hasSize(1);
    }
}