package br.com.campanha.controller;

import br.com.campanha.base.IntegrationBaseTest;
import br.com.campanha.repository.CampanhaRepository;
import br.com.campanha.vo.CampanhaVO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CampanhaControllerTest extends IntegrationBaseTest {

    @Autowired
    private CampanhaController campanhaController;

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Before
    public void setUp() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    public void cadastrarCampanhaTest() {
        ResponseEntity<?> responseEntity = campanhaController.cadastrarCampanha(new CampanhaVO("campanha 4", 1,LocalDate.now(), LocalDate.now().plusDays(5), null));
        assertThat(responseEntity).as("A campanha deve ser criada com sucesso").isNotNull();
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser created").isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().get("location")).as("Deve retornar a URL da campanha criada").isNotNull();
    }

    @Test
    public void deveRetornarQuandoHouverCampanhasAtivas() {
        ResponseEntity<List<CampanhaVO>> campanhas = campanhaController.buscarTodasCampanhas();
        assertThat(campanhas.getBody()).as("Deve retornar as 4 campanhas ativas").hasSize(4);
    }


    @Test
    public void buscarPorId() {
        final ResponseEntity<CampanhaVO> responseEntity = campanhaController.buscarPorId(1);
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser OK").isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody()).as("Deve retornar a Campanha e todos os seus dados")
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    public void deletarPorId() {
        final ResponseEntity<?> responseEntity = campanhaController.deletarPorId(3);
        assertThat(responseEntity.getStatusCode()).as("O Status code deve ser NO_CONTENT").isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(campanhaRepository.findById(3)).as("A campanha tem que ser deletada").isEmpty();
    }



    @Test
    public void handleValidationException() {
        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> campanhaController.cadastrarCampanha(new CampanhaVO(null, 1,LocalDate.now(), LocalDate.now().plusDays(5), null)));
    }
}