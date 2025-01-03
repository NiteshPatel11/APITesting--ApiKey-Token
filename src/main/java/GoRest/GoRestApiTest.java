package GoRest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
public class GoRestApiTest {
 private static String baseUrl;
    private static String accessToken;

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

    @Test

    public void GetUser(){

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

        String requestBody = """
                {
                    "name": "Test User",
                    "gender": "male",
                    "email": "testuser123@example.com",
                    "status": "active"
                }
                """;

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test User"))
                .body("gender", equalTo("male"))
                .body("email", equalTo("testuser123@example.com"))
                .body("status", equalTo("active"))
                .extract()
                .response();


        System.out.println("Response: " + response.asPrettyString());
    }


   @Test
    public void UpdateUser(){
        int userId = 7616156;


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
               .put("/users/" + userId)
               .then()
               .statusCode(200)
               .body("name", equalTo("anand"))
               .extract()
               .response();

       System.out.println("Response: " + response.asPrettyString());

     }

     @Test
    public void GetUserById(){

        int userId =  7616155;
         Response response = given()
                 .header("Authorization", "Bearer " + accessToken)
                 .when()
                 .get("/users/" + userId);
                   response.then()
                 .statusCode(200)
                 .body("id", equalTo(userId));


         int returnedUserId = response.jsonPath().getInt("id");
         System.out.println("User ID retrieved: " + returnedUserId);
     }

    @Test
    public void  DeleteUserById() {
        int userId =  7616154 ;
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/users/" + userId);

                 response.then()
                .statusCode(204);

        System.out.println("Delete user response: " + response.getBody().asString());
    }
 }




