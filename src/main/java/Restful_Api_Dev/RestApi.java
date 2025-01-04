package Restful_Api_Dev;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RestApi {
    private static String baseUrl;
    private static String createdUserId;

    @BeforeAll
    public static void setup() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(input);
        }

        baseUrl = properties.getProperty("api.service2.url");

        RestAssured.baseURI = baseUrl;
    }

    @BeforeEach
    public void createUserNotExist() {
        if (createdUserId == null || createdUserId.equals("0")) {
            createUser();
        }
    }

    @Test
    public void GetAllUsers() {
        Response response = given()
                .when()
                .get("objects")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .response();
        System.out.println("Response: " + response.asPrettyString());
    }

    @Test
    public void createUser() {
        // Correct JSON body format with data field included
        String requestBody = """
                {
                    "name": "Apple iPhone 19, 842GB",
                    "data": {
                        "price": 389.99,
                        "color": "Purple"
                    }
                }
                """;

        // Send POST request to create a new user
        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/objects")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", not(" "))
                .body("name", equalTo("Apple iPhone 19, 842GB"))
                .extract()
                .response();



        System.out.println("Response Status: " + response.statusCode());
        System.out.println("Response Body: " + response.asPrettyString());

        // Extract the id from
        createdUserId = response.jsonPath().getString("id");
        System.out.println("Created User ID: " + createdUserId);
    }
}
