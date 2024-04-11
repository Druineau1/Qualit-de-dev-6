package iut.java.spring.tests.integration;

import iut.java.spring.dto.IndividuDto;
import iut.java.spring.service.interfaces.IIndividuService;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IndividuServiceTest {

    @Autowired
    private IIndividuService service;

    @Autowired
    private DataSource dataSource;

    @Test
    void testGetList() throws Exception {
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        List<String> prenomsAttendus = List.of(
                "Louis",
                "Anthia",
                "Sondra",
                "Elie",
                "Madlin",
                "Aloysia",
                "Brook",
                "Herman",
                "Rafaellle",
                "Maude"
        );

        List<IndividuDto> individus = service.getList();

        assertThat(individus).extracting(IndividuDto::getFirstName).containsAll(prenomsAttendus);
    }
}

