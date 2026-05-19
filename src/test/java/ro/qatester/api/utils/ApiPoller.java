package ro.qatester.api.utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static java.time.Duration.ofSeconds;

public class ApiPoller {

    public static String waitForConfirmationToken(String apiKey,
                                                  String usernameOrEmail) {

        await()
                .atMost(ofSeconds(30))
                .pollDelay(ofSeconds(5))
                .pollInterval(ofSeconds(2))
                .until(() -> {

                    Response response =
                            given()
                                    .header("Authorization", apiKey)
                                    .queryParam("username_or_email",
                                            usernameOrEmail)
                                    .when()
                                    .get("/user/retrieve_token.php");

                    String token =
                            response.jsonPath()
                                    .getString("confirmation_token");

                    return token != null && !token.isEmpty();
                });

        Response finalResponse =
                given()
                        .header("Authorization", apiKey)
                        .queryParam("username_or_email",
                                usernameOrEmail)
                        .when()
                        .get("/user/retrieve_token.php");

        return finalResponse
                .jsonPath()
                .getString("confirmation_token");
    }
}