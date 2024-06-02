package order.create;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserLogin;
import java.net.HttpURLConnection;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class PositiveCreateOrder {
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String DELETE_PATH = "/api/auth/user";
    public static final String USER_PATH = "/api/auth/register";
    public static final String INGREDIENTS_PATH = "/api/ingredients";
    public static final String ORDER_PATH = "/api/orders";
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
    @DisplayName("Позитивный тест: создание заказа с ингредиентами авторизованного пользователя")
    public void testCreateOrder() {
        User user = User.createdUser();
        var login = UserLogin.from(User.createdUser());

                given().log().all()
                        .header("Content-type", "application/json")            // Регестрируемся
                        .and()
                        .body(user)
                        .when()
                        .post(USER_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .path("success");
        var response =
                given().log().all()
                        .header("Content-type", "application/json")          // Авторизовываемся
                        .and()
                        .body(login)
                        .when()
                        .post(LOGIN_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .response();
        accessToken = response.path("accessToken");

        var ingredientsResponse =
                given().log().all()
                        .header("Authorization", accessToken)               // Запрашиваем список ингридиентов
                        .when()
                        .get(INGREDIENTS_PATH);
                response.then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .response();

        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");

        String ingredientId1 = ingredientIds.get(0);                        // Выбираем ингредиенты
        String ingredientId2 = ingredientIds.get(1);
        String ingredientId3 = ingredientIds.get(6);
        String ingredientId4 = ingredientIds.get(14);


                given().log().all()                                         //Создаем заказ
                        .header("Authorization", accessToken)
                        .header("Content-type", "application/json")
                        .body("{ \"ingredients\": [\"" + ingredientId1 + "\", \"" + ingredientId2 + "\", " +
                                "\"" + ingredientId3 + "\", \"" + ingredientId4 + "\"] }")
                        .when()
                        .post(ORDER_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)          // Разобраться со статусами
                        .body("success", equalTo(true));
    }
}
