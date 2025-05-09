// package id.cs.ui.advprog.inthecost.repository;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import javax.sql.DataSource;

// import static org.junit.jupiter.api.Assertions.assertNotNull;

// @SpringBootTest
// public class DatabaseConnectionTest {

//     @Autowired
//     private DataSource dataSource;

//     @Test
//     void testDatabaseConnection() throws Exception {
//         assertNotNull(dataSource, "DataSource should not be null");
//         try (var connection = dataSource.getConnection()) {
//             assertNotNull(connection, "Database connection should be established");
//         }
//     }
// }