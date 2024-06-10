package user;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

public class UserMethod {
    public static final String USER_PATH = "/api/auth/register";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String UPDATE_PATH = "/api/auth/user";
    public static final String DELETE_PATH = "/api/auth/user";
   @Step("Регистрация пользователя")
    public ValidatableResponse userRegistration(User user) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(USER_PATH)
                .then().log().all();
    }
    @Step("Проверка успешной регистрации пользователя")
    public Response successfulCreated(ValidatableResponse createResponse) {
        var response = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK) //При создании сущности думаю должен быть код ответа 201, но в документации 200.
                .extract()
                .response();

        boolean created = response.path("success");
        assertTrue(created);
        return response;
    }
    @Step("Извленение accessToken")
    public String extractToken(Response response) {
        return response.path("accessToken");

    }
    @Step("Проверка ответа системы на дублирование пользователя")
    public void creationError(ValidatableResponse duplicateResponse) {
        duplicateResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }
    @Step("Проверка ответа системы, если одного из обязательных полей нет")
    public ValidatableResponse withoutName(ValidatableResponse errorResponse) {
        return errorResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Step("Вход пользователя в систему")
    public ValidatableResponse userLogin(UserLogin login) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(login)
                .when()
                .post(LOGIN_PATH)
                .then().log().all();
    }
    @Step("Проверка успешной авторизации")
    public Response authorization(ValidatableResponse loginResponse) {
        var response = loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract()
                .response();
        return response;
    }
    @Step("Проверка ответа системы на ввод невалидных данных")
    public ValidatableResponse invalidCredentials(ValidatableResponse loginResponse) {
        return loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }
    @Step("Авторизация пользователя")
    public String userAuthorization(UserLogin login) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post(LOGIN_PATH)
                .then().log().all()
                .extract()
                .path("accessToken");
    }
    @Step("Изменения данных пользователя")
    public ValidatableResponse сhangeDataUser(User updateUser, String accessToken) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(updateUser)
                .when()
                .patch(UPDATE_PATH)
                .then().log().all();
    }
    @Step("Проверка на успешное изменения данных")
    public ValidatableResponse updateSuccess(ValidatableResponse updateResponse) {
        return updateResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true));
    }
    @Step("Изменения данных не авторизованного пользователя")
    public ValidatableResponse updateDadaWithoutAuth(User updateUser) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .body(updateUser)
                .when()
                .patch(UPDATE_PATH)
                .then().log().all();

    }
    @Step("Проверка ответа системы на попытку изменить данные без авторизации")
    public ValidatableResponse errorAuthorization(ValidatableResponse errorResponse) {
        return errorResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
    @Step("Удаление пользователя")
    public ValidatableResponse userDelete(String accessToken) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_PATH)
                .then().log().all();
    }
    @Step("Успешное удаление пользователя")
    public void deleteSuccesfully(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_ACCEPTED);
    }
}