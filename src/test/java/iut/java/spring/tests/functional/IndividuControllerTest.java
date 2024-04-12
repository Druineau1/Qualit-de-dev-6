package iut.java.spring.tests.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dbunit.Assertion.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementTable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import iut.java.spring.dto.IndividuDto;
import iut.java.spring.entity.Individu;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class IndividuControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testGet() throws Exception {
    	//ARRANGE initialisation de la base de données  
        String path = "/individu/{id}";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        
        //ACT requête http pour récupérer l'individu
        IndividuDto individu = client.get().uri(path, 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividuDto.class)
                .returnResult().getResponseBody();
        //ASSERT vérification de l'individu
        assertNotNull(individu.getId());
        assertThat(individu.getFirstName()).isEqualTo("Louis");
        assertThat(individu.getLastName()).isEqualTo("Morison");
        assertThat(individu.getTitle()).isEqualTo("Honorable");
        assertThat(individu.getHeight()).isEqualTo(178);
        assertThat(individu.getBirthDate()).isEqualTo(LocalDate.of(2006, 5, 3));

    }
    @Test
    public void testRemove() throws Exception {
        //ARRANGE initialisation de la base de données 
    	String path = "/individu/{id}";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();

        //ACT requête http pour supprimer l'individu
        client.delete().uri(path, 2L)
                .exchange()
                .expectStatus().isOk();

        //ASSERT récupération d'une base de données qui contient le résultat souhaité
        InputStream ist = getClass().getClassLoader()
                .getResourceAsStream("VerifRemove.xml");
        IDataSet dataSett = new FlatXmlDataSetBuilder().build(ist);
        ITable expectedTable = dataSett.getTable("individu");
        IDataSet dbDataSet = new DatabaseDataSourceConnection(dataSource)
                .createDataSet();
        ITable actualTable = dbDataSet.getTable("individu");
        // comparaison entre la base de données ayant subit la reqête et celle de vérification
        assertEquals(expectedTable,actualTable);
    }
    

    @Test
    public void testAdd() throws Exception {
        //ARRANGE initialisation de la base de données 
        String path = "/individu";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        //création d'un individu
        IndividuDto individuAdd = new IndividuDto();
        individuAdd.setFirstName("Louis");
        individuAdd.setLastName("Pinax");
        individuAdd.setTitle("Ingineer");
        individuAdd.setHeight(180);
        individuAdd.setBirthDate(LocalDate.of(1985, 10, 15));


        //ACT requête pour ajouter un individu
        IndividuDto individu = client.post().uri(path)
                .body(BodyInserters.fromValue(individuAdd))
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividuDto.class)
                .returnResult().getResponseBody();
        //ASSERT récupération d'une base de données qui contient le résultat souhaité
        InputStream is1 = getClass().getClassLoader()
                .getResourceAsStream("VerifAdd.xml");
        IDataSet dataSet1 = new FlatXmlDataSetBuilder().build(is1);
        ITable expectedTable = dataSet1.getTable("individu");
        ReplacementTable replacementTable = new ReplacementTable(expectedTable);
        replacementTable.addReplacementObject("[ID]", individu.getId());
        IDataSet dbDataSet = new DatabaseDataSourceConnection(dataSource)
                .createDataSet();
        ITable actualTable = dbDataSet.getTable("individu");
        // comparaison entre la base de données ayant subit la reqête et celle de vérification
        assertEquals(replacementTable, actualTable);
    }
    
    @Test
    public void testModifyFound() throws Exception {
    	//ARRANGE initialisation de la base de données 
        String path = "/individu";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();
        //création d'un individu
        IndividuDto individuMod = new IndividuDto();
        individuMod.setId(10L);
        individuMod.setFirstName("Louis");
        individuMod.setLastName("Pinax");
        individuMod.setTitle("Ingineer");
        individuMod.setHeight(180);
        individuMod.setBirthDate(LocalDate.of(1985, 10, 15));


        //ACT requête http pour modifier l'individu avec l'id 10
        client.put().uri(path)
                .body(BodyInserters.fromValue(individuMod))
                .exchange()
                .expectStatus().isOk();

        //ASSERT récupération d'une base de données qui contient le résultat souhaité
        InputStream is1 = getClass().getClassLoader()
                .getResourceAsStream("VerifModify.xml");
        IDataSet dataSet1 = new FlatXmlDataSetBuilder().build(is1);
        ITable expectedTable = dataSet1.getTable("individu");
        IDataSet dbDataSet = new DatabaseDataSourceConnection(dataSource)
                .createDataSet();
        ITable actualTable = dbDataSet.getTable("individu");
        // comparaison entre la base de données ayant subit la reqête et celle de vérification
        assertEquals(expectedTable, actualTable);
    }/*
    
    @Test
    public void testModifyNotFound() throws Exception {
    	//ARRANGE initialisation de la base de données 
        String path = "/individu";
        IDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);
        tester.setDataSet(dataSet);
        tester.onSetup();

        
        IndividuDto individuModNotF = new IndividuDto();
        individuModNotF.setId(11L);
        individuModNotF.setFirstName("Louis");
        individuModNotF.setLastName("Pinax");
        individuModNotF.setTitle("Ingineer");
        individuModNotF.setHeight(180);
        individuModNotF.setBirthDate(LocalDate.of(1985, 10, 15));


        //ACT
        client.put().uri(path)
                .body(BodyInserters.fromValue(individuModNotF))
                .exchange()
                .expectStatus().isNotFound();

        //ASSERT
        InputStream is1 = getClass().getClassLoader()
                .getResourceAsStream("DruineauThomas.xml");
        IDataSet dataSet1 = new FlatXmlDataSetBuilder().build(is1);
        ITable expectedTable = dataSet1.getTable("individu");
        IDataSet dbDataSet = new DatabaseDataSourceConnection(dataSource)
                .createDataSet();
        ITable actualTable = dbDataSet.getTable("individu");
        assertEquals(expectedTable, actualTable);
    }*/
}

