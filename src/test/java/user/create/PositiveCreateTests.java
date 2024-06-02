package user.create;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import user.User;

import java.net.HttpURLConnection;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertTrue;

public class PositiveCreateTests {

    public static final String USER_PATH = "/api/auth/register";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Позитивный тест: создание уникального пользователя")
    public void createUser() {
        User user = User.createdUser();
        boolean created =
                given().log().all()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(USER_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .path("success");

        assertTrue(created);

    }

}