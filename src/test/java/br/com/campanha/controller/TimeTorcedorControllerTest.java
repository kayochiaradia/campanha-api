package br.com.campanha.controller;

import br.com.campanha.base.IntegrationBaseTest;
import br.com.campanha.repository.TimeRepository;
import br.com.campanha.vo.TimeVO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeTorcedorControllerTest extends IntegrationBaseTest {

    @Autowired
    private TimeTorcedorController timeTorcedorController;

    @Autowired
    private TimeRepository timeRepository;

    @Before
    public void setUp() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    public void cadastrarTimeTest() {
        ResponseEntity<?> responseEntity = timeTorcedorController.cadastrarTime(new TimeVO("Nova Paisandu",null));
        assertThat(responseEntity).as("O time deve ser criado com sucesso").isNotNull();
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser created").isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().get("location")).as("Deve retornar a URL do time criado").isNotNull();
    }

    @Test
    public void retornarTodosOsTimesCadastrados() {
        ResponseEntity<List<TimeVO>> timeVO = timeTorcedorController.buscarTodosOsTimes();
        assertThat(timeVO.getBody()).as("Deve retornar as 4 times cadastrado").hasSize(3);
    }


    @Test
    public void buscarPorId() {
        final ResponseEntity<TimeVO> responseEntity = timeTorcedorController.buscarPorId(1);
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser OK").isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody()).as("Deve retornar o Time e todos os seus dados")
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    public void deletarPorId() {
        final ResponseEntity<?> responseEntity = timeTorcedorController.deletarPorId(3);
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser NO_CONTENT").isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(timeRepository.findById(3)).as("A campanha tem que ser deletada").isEmpty();
    }

}
