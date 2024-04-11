package iut.java.spring.tests.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import iut.java.spring.dto.IndividuDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class IndividuControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private DataSource dataSource;

    @Test
    void testGet() throws Exception {
    	//ARRANGE
        Long id = 1L;
        String path = "/individu/{id}";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        
        //ACT
        IndividuDto individu = client.get().uri(path, id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividuDto.class)
                .returnResult().getResponseBody();
        //ASSERT
        assertNotNull(individu.getId());
        assertThat(individu.getFirstName()).isEqualTo("Louis");
        assertThat(individu.getLastName()).isEqualTo("Morison");
        assertThat(individu.getTitle()).isEqualTo("Honorable");
        assertThat(individu.getHeight()).isEqualTo(178);
        assertThat(individu.getBirthDate()).isEqualTo(LocalDate.of(2006, 5, 3));

    }
    @Test
    void testRemove() throws Exception {
        //ARRANGE
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();

        //ACT
        //On exécute la requête HTTP DELETE pour supprimer un individu avec l'id 2
        client.delete().uri("/individu/{id}", 2L)
                .exchange()
                .expectStatus().isOk();

        //ASSERT
        InputStream ist = getClass().getClassLoader()
                .getResourceAsStream("VerifRemove.xml");
        IDataSet dataSett = new FlatXmlDataSetBuilder().build(ist);
        ITable expectedTable = dataSett.getTable("individu");
        IDataSet dbDataSet = new DatabaseDataSourceConnection(dataSource)
                .createDataSet();
        ITable actualTable = dbDataSet.getTable("individu");
        assertEquals(expectedTable,actualTable);
    }
    
    @Test
    public void testAdd() throws Exception {
        //ARRANGE
        String path = "/individu";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();

        IndividuDto individuAdd = new IndividuDto();
        individuAdd.setId(11L);
        individuAdd.setFirstName("Louis");
        individuAdd.setLastName("Pinax");
        individuAdd.setTitle("Ingineer");
        individuAdd.setHeight(180);
        individuAdd.setBirthDate(LocalDate.of(1985, 10, 15));


        //ACT
        IndividuDto individu = client.post().uri(path) // Fournir la valeur de l'ID
                .body(BodyInserters.fromValue(individuAdd))
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividuDto.class)
                .returnResult().getResponseBody();
        //ASSERT
	
         assertNotNull(individu.getId());
         assertThat(individu.getFirstName()).isEqualTo("Louis");
         assertThat(individu.getLastName()).isEqualTo("Pinax");
         assertThat(individu.getTitle()).isEqualTo("Ingineer");
         assertThat(individu.getHeight()).isEqualTo(180);
         assertThat(individu.getBirthDate()).isEqualTo(LocalDate.of(1985, 10, 15));


    }
}
