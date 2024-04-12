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
    public void testGetList() throws Exception {
    	//ARRANGE initialisation de la base de données 
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        //création de la liste des prenoms attendus
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
        //ACT récupération de la liste
        List<IndividuDto> individus = service.getList();
        //ASSERT vérification que le fichier xml contient bien les prenoms attendus
        assertThat(individus).extracting(IndividuDto::getFirstName).containsAll(prenomsAttendus);
    }
}

