package br.com.campanha.base;

import br.com.campanha.CampanhaApplication;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CampanhaApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationBaseTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Value("${local.server.port}")
    protected Integer port;

    public String getBaseUrl(){
        return String.format("http://localhost:%d", port);
    }


}