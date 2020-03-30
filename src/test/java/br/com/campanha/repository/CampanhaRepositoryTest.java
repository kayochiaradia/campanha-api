package br.com.campanha.repository;

import br.com.campanha.base.IntegrationBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CampanhaRepositoryTest extends IntegrationBaseTest {

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Test
    public void deveTrazerCampanhasComDataDeFimVigenciaoSuperiorADataDoParametro() {


        assertThat(campanhaRepository.findByFimVigenciaIsGreaterThanEqual(LocalDate.now().plusDays(5)))
                .as("Deve Trazer somente a Campanha 1 - que esta ativa")
                .hasSize(1);

        assertThat(campanhaRepository.findByFimVigenciaIsGreaterThanEqual(LocalDate.now().plusDays(4)))
                .as("Deve Trazer as duas(2) Campanhas (1 e 2) que tem datas de fim vigÊncia inferior ao parâmetro")
                .hasSize(2);
    }

    @Test
    public void naoDeveTrazerCampanhasComDataDeFimVigenciaoInferiorADataDoParametro() {

        assertThat(campanhaRepository.findByFimVigenciaIsGreaterThanEqual(LocalDate.of(2020, 9, 2)))
                .as("Nenhuma campanha deve ser retornada pois não existem Campanhas ativas (Data de inicio de " +
                        "vegência é maior que data do parâmetro)")
                .isNullOrEmpty();


        assertThat(campanhaRepository.findByFimVigenciaIsGreaterThanEqual(LocalDate.of(2020, 10, 5)))
                .as("Nenhuma campanha deve ser retornada pois todas as campanhas cadastradas no sistema estao " +
                        "vencidas (Data de fim vigência é menor que a data do parametro)")
                .isNullOrEmpty();
    }

    @Test
    public void deveTrazerCampanhasAtivasPorPeriodo() {


        assertThat(campanhaRepository.findByFimVigenciaBetween(
                LocalDate.now(), LocalDate.now().plusDays(3)))
                .as("Deve trazersomente uma Campanha (Campanha 2) dado que somente ela esta dentro da vigência deste período")
                .hasSize(1);

        assertThat(campanhaRepository.findByFimVigenciaBetween(
                LocalDate.now(), LocalDate.now().plusDays(4)))
                .as("Deve trazer as duas Campanhas ativas dado que todas estão com a vigência dentro deste período")
                .hasSize(2);
    }

    @Test
    public void naoDeveTrazerCampanhasQuandoPeriodoEstiverForaDosParametros() {

        assertThat(campanhaRepository.findByFimVigenciaBetween(
               LocalDate.now().plusDays(20), LocalDate.now().plusDays(30)))
                .as("Não existe nenhuma campanha vigênte neste período")
                .isEmpty();
    }

    @Test
    public void findByTimeCoracaoIdTest() {
        assertThat(campanhaRepository.
                findByTime_IdAndInicioVigenciaIsLessThanEqualAndFimVigenciaIsGreaterThanEqual
                        (1, LocalDate.now(), LocalDate.now().plusDays(5)))
                .as("Deve encontrar uma Campanha com o Time 1")
                .hasSize(1);
    }
}