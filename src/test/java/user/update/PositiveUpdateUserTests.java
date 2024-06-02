package user.update;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserLogin;

import java.net.HttpURLConnection;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class UpdateUserTests {
    public static final String DELETE_PATH = "/api/auth/user";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String UPDATE_PATH = "/api/auth/user";
    private String accessToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @After
    public void deleteUser() {
        if (accessToken != null) {
            given().log().all()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .when()
                    .delete(DELETE_PATH)
                    .then()
                    .statusCode(HttpURLConnection.HTTP_ACCEPTED);
        }
    }
    @Test
    @DisplayName("Позитивный тест: изменение данных пользователя с авторизацией")
    public void updateUserWithAuth() {
        var login = UserLogin.from(User.createdUser());
        User newName = User.updatedUser();
        accessToken =
                given().log().all()
                        .header("Content-type", "application/json")
                        .and()
                        .body(login)
                        .when()
                        .post(LOGIN_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .path("accessToken");

        boolean updated =
                given().log().all()
                        .header("Content-type", "application/json")
                        .and()
                        .header("Authorization", accessToken)
                        .and()
                        .body(newName)
                        .when()
                        .patch(UPDATE_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .path("success");

        assertTrue(updated);

        }
}
