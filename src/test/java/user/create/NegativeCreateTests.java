package user.create;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import user.User;
import java.net.HttpURLConnection;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
public class NegativeCreateTests {
    public static final String USER_PATH = "/api/auth/register";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("Негативный тест: создание пользователя, который уже зарегистрирован")
    public void testDuplicateUser() {
        User user = User.createdUser();

        given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_PATH)
                .then().log().all()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK);

        given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_PATH)
                .then().log().all()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }
    @Test
    @DisplayName("Негативный тест: создание пользователя без одного обязательного поля.")
    public void testMissingFields() {
        User withoutName = User.noRequiredField();

        given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(withoutName)
                .when()
                .post(USER_PATH)
                .then().log().all()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));

    }
}

