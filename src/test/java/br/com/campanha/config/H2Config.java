package br.com.campanha.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile("test")
@Configuration
public class H2Config {

    @Autowired
    private DataSource datasource;

    @PostConstruct
    public void loadSQL() throws Exception {
        ScriptUtils.executeSqlScript(datasource.getConnection(), new ClassPathResource("/sql/load_database.sql"));
    }
}