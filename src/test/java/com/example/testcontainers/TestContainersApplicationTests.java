package com.example.testcontainers;
import com.example.testcontainers.entity.User;
import com.example.testcontainers.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TestContainersApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("users_db")
            .withUsername("postgres")
            .withPassword("1");


    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl); // ⬅️ динамический URL с правильным портом!
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // ✅ Добавьте эти строки:
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");


    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        userRepository.deleteAll(); // Очищаем БД перед каждым тестом
    }

    @Test
    void testFindAll() {
        User user1=new User();
        user1.setName("User1");
        userRepository.save(user1);

        User user2=new User();
        user2.setName("User2");
        userRepository.save(user2);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].name", equalTo("User1")) // Проверяем первый элемент
                .body("[1].name", equalTo("User2")); // Проверяем второй элемент

    }

    @Test
    void testSave() {
        User user=new User();
        user.setName("User1");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/users/add")
                .then()
                .statusCode(201)
                .body("name", equalTo("User1"));
    }

    @Test
    void testFindById() {
        User user1=new User();
        user1.setName("User111");
        userRepository.save(user1);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user1)
                .when()
                .get("/api/users/{id}", user1.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo("User111"));
    }
    @Test
    void testDeleteById() {
        Long id=1L;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userRepository.findById(id))
                .when()
                .delete("/api/users/{id}", id)
                .then()
                .statusCode(204);
    }

    @Test
    void testUpdateById() {
        User user=new User();
        user.setName("User1");
        User savedUser = userRepository.save(user);
        Long id=savedUser.getId();

        User updateUser=new User();
        updateUser.setId(id);
        updateUser.setName("User111");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateUser)
                .when()
                .put("/api/users/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("User111"));
    }

    @Test
    void testPatchById() {
        User user=new User();
        user.setName("User1");
        User savedUser = userRepository.save(user);
        Long id=savedUser.getId();

        User patchUser=new User();
        patchUser.setId(id);
        patchUser.setName("User111");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(patchUser)
                .when()
                .patch("/api/users/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("User111"));
    }


}
