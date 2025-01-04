package GoRest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GoRestApiTest {
    private static String baseUrl;
    private static String accessToken;
    private static int createdUserId;

    @BeforeAll
    public static void setup() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(input);
        }

        baseUrl = properties.getProperty("baseUrl");
        accessToken = properties.getProperty("accessToken");

        RestAssured.baseURI = baseUrl;
    }
    @BeforeEach
    public void createUserIfNotExist() {

        if (createdUserId == 0) {
            createUser();
        }
    }

    @Test
    public void GetUser() {
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .response();

        System.out.println("Response: " + response.asPrettyString());
    }

    @Test
    public void createUser() {
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";

        String requestBody = """
                {
                    "name": "ndgye",
                    "gender": "male",
                    "email": "%s",
                    "status": "active"
                }
                """.formatted(uniqueEmail);

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("ndgye"))
                .body("gender", equalTo("male"))
                .body("email", equalTo(uniqueEmail))
                .body("status", equalTo("active"))
                .extract()
                .response();

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.asPrettyString());

        createdUserId = response.jsonPath().getInt("id");
        System.out.println("Created User ID: " + createdUserId);
    }

    @Test
    public void UpdateUser() {

        if (createdUserId == 0) {
            throw new IllegalStateException("User not created, skipping update test.");
        }

        String requestBody = """
                {
                    "name": "anand",
                    "status": "inactive"
                }
                """;

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/users/" + createdUserId)
                .then()
                .statusCode(200)
                .body("name", equalTo("anand"))
                .body("status", equalTo("inactive"))
                .extract()
                .response();

        System.out.println("Response: " + response.asPrettyString());
    }

    @Test
    public void GetUserById() {
        // Ensure createUser is run first and createdUserId is set
        if (createdUserId == 0) {
            throw new IllegalStateException("User not created, skipping get user by ID test.");
        }

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users/" + createdUserId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUserId))
                .extract()
                .response();

        System.out.println("User ID retrieved: " + response.jsonPath().getInt("id"));
    }

    @Test
    public void DeleteUserById() {
         if (createdUserId == 0) {
            throw new IllegalStateException("User not created, skipping delete test.");
        }

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/users/" + createdUserId)
                .then()
                .statusCode(204)
                .extract()
                .response();

        System.out.println("Delete user response: " + response.getBody().asString());
    }
}
