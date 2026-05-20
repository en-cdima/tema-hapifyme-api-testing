package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.models.*;
import ro.qatester.api.utils.ApiPoller;
import ro.qatester.api.utils.DataGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;

public class UserLifecycleTest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String apiKey;
    private String confirmationToken;
    private String username;
    private String userId;
    private String bearerToken;
    private String updatedFirstName;
    private String updatedLastName;
    private String updatedEmail;

    @BeforeClass
    public void setup() {

        RestAssured.baseURI =
                "https://apps.qualiadept.eu/hapifyme/api";

        password = System.getenv("TEST_PASSWORD");

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("TEST_PASSWORD environment variable is not set.");
        }

        firstName = DataGenerator.generateFirstName();
        lastName = DataGenerator.generateLastName();
        email = DataGenerator.generateEmail();
        password = DataGenerator.generatePassword();
        updatedFirstName = DataGenerator.generateUpdatedFirstName();
        updatedLastName = DataGenerator.generateUpdatedLastName();
        updatedEmail = DataGenerator.generateUpdatedEmail();

    }

    @Test(priority = 1)
    public void shouldRegisterUserSuccessfully() {

        RegisterRequest registerRequest =
                new RegisterRequest(
                        firstName,
                        lastName,
                        email,
                        password
                );

        RegisterResponse registerResponse =

                given()
                        .contentType(ContentType.JSON)
                        .body(registerRequest)
                        .log().ifValidationFails()
                        .when()
                        .post("/user/register.php")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract()
                        .as(RegisterResponse.class);

        assertEquals(registerResponse.getStatus(), "success");

        apiKey = registerResponse.getApi_key();
        username = registerResponse.getUsername();
        userId = registerResponse.getUser_id();

        assertNotNull(apiKey);
        assertNotNull(username);
        assertNotNull(userId);

        confirmationToken =
                ApiPoller.waitForConfirmationToken(apiKey,
                        username
                );

        assertNotNull(confirmationToken);
        assertFalse(confirmationToken.isEmpty());
    }

    @Test(priority = 2)
    public void shouldConfirmUserEmailSuccessfully() throws InterruptedException {

        assertNotNull(confirmationToken, "Confirmation token must be available before confirmation.");

        given()
                .queryParam("token", confirmationToken)
                .log().all()
                .when()
                .get("/user/confirm_email.php")
                .then()
                .log().body()
                .statusCode(200)
                .body("status", equalTo("success"));
    }

    @Test(priority = 3, dependsOnMethods = "shouldConfirmUserEmailSuccessfully")
    public void shouldLoginUserSuccessfully() {

        LoginRequest loginRequest =
                new LoginRequest(username, password);

        LoginResponse loginResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(loginRequest)
                        .log().ifValidationFails()
                        .when()
                        .post("/user/login.php")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(LoginResponse.class);

        assertEquals(loginResponse.getStatus(), "success");

        bearerToken = loginResponse.getToken();

        assertNotNull(bearerToken);
        assertFalse(bearerToken.isEmpty());
    }

    @Test(priority = 4, dependsOnMethods = "shouldLoginUserSuccessfully")
    public void shouldGetUserProfileSuccessfully() {

        ProfileResponse profileResponse =
                given()
                        .header("Authorization", apiKey)
                        .queryParam("user_id", userId)
                        .log().all()
                        .when()
                        .get("/user/get_profile.php")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(ProfileResponse.class);

        assertEquals(profileResponse.getStatus(), "success");
        assertNotNull(profileResponse.getUser());

        assertEquals(profileResponse.getUser().getId(), userId);
        assertEquals(profileResponse.getUser().getEmail(), email);
        assertEquals(profileResponse.getUser().getFirst_name(), firstName);
        assertEquals(profileResponse.getUser().getLast_name(), lastName);
        assertEquals(profileResponse.getUser().getUsername(), username);

        assertNotNull(profileResponse.getUser().getSignup_date());
        assertNotNull(profileResponse.getUser().getProfile_pic());
    }

    @Test(priority = 5, dependsOnMethods = "shouldGetUserProfileSuccessfully")
    public void shouldUpdateUserProfileSuccessfully() {

        UpdateProfileRequest updateProfileRequest =
                new UpdateProfileRequest(
                        userId,
                        updatedFirstName,
                        updatedLastName,
                        updatedEmail
                );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", apiKey)
                .body(updateProfileRequest)
                .log().all()
                .when()
                .put("/user/update_profile.php")
                .then()
                .log().body()
                .statusCode(200)
                .body("status", equalTo("success"));
    }

    @Test(priority = 6, dependsOnMethods = "shouldUpdateUserProfileSuccessfully")
    public void shouldValidateUpdatedUserProfileSuccessfully() {

        ProfileResponse profileResponse =
                given()
                        .header("Authorization", apiKey)
                        .queryParam("user_id", userId)
                        .log().all()
                        .when()
                        .get("/user/get_profile.php")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract()
                        .as(ProfileResponse.class);

        assertEquals(profileResponse.getStatus(), "success");
        assertNotNull(profileResponse.getUser());

        assertEquals(profileResponse.getUser().getId(), userId);
        assertEquals(profileResponse.getUser().getFirst_name(), updatedFirstName);
        assertEquals(profileResponse.getUser().getLast_name(), updatedLastName);
        assertEquals(profileResponse.getUser().getEmail(), updatedEmail);
        assertEquals(profileResponse.getUser().getUsername(), username);
    }

    @Test(priority = 7,
            dependsOnMethods = "shouldValidateUpdatedUserProfileSuccessfully")
    public void shouldDeleteUserProfileSuccessfully() {

        given()
                .header("Authorization", "Bearer " + bearerToken)
                .log().all()

                .when()
                .delete("/user/delete_profile.php")

                .then()
                .log().body()
                .statusCode(200)
                .body("status", equalTo("success"));
    }

    @Test(priority = 8,
            dependsOnMethods = "shouldDeleteUserProfileSuccessfully")
    public void shouldNotRetrieveDeletedUserProfile() {

        given()
                .header("Authorization", apiKey)
                .queryParam("user_id", userId)
                .log().all()

                .when()
                .get("/user/get_profile.php")

                .then()
                .log().body()
                .statusCode(200)
                .body("status", equalTo("error"))
                .body("message", equalTo("User not found."));
    }

}