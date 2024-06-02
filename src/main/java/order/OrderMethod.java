package order;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.net.HttpURLConnection;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class OrderMethod {
    public static final String ORDER_PATH = "/api/orders";
    public static final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Получения списка ингридиентов")
    public Response getIngredientsResponse() {
        var ingredientsResponse =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .when()
                        .get(INGREDIENTS_PATH)
                        .then().log().all()
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)
                        .extract()
                        .response();
        return ingredientsResponse;
    }
    @Step("Получение ID ингредиентов из ответа и формирование списка")
    public List<String> getIngredientIds(Response ingredientsResponse) {
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
        return ingredientIds;
    }
    @Step("Получаем первые три ингредиента для дальнейшего формирования заказа")
    public List<String> getFirstThreeIngredients(List<String> ingredientIds) {      // Возвращаем первые три ингредиента
        return ingredientIds.subList(0, 3);
    }
   @Step("Авторизовываемся и создаем заказ")
    public ValidatableResponse createOrder(List<String> ingredientIds, String accessToken) {
            return given().log().all()
                        .header("Authorization", accessToken)
                        .and()
                        .contentType(ContentType.JSON)
                        .body(new Ingredient(ingredientIds))
                        .when()
                        .post(ORDER_PATH)
                        .then().log().all();
    }
    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(List<String> ingredientIds) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .body(new Ingredient(ingredientIds))
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }
    @Step("Проверка создания заказа")
    public void createSuccessful(ValidatableResponse orderResponse) {
                orderResponse
                        .assertThat()
                        .statusCode(HttpURLConnection.HTTP_OK)    //При создании сущности, в данном случае заказа, думаю статус ответа должен быть 201, но в документации 200.
                        .body("success", equalTo(true));
    }
    @Step("Создание заказа без ингридиентов")
    public ValidatableResponse createOrderWithoutIngredients(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .and()
                .contentType(ContentType.JSON)
                .body("{ \"ingredients\": [] }")
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }
    @Step("Проверка что заказ не создается без id ингредиентов")
    public void errorCreateOrder(ValidatableResponse noIngredientsResponse) {
        noIngredientsResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @Step("Создание заказа с неверным хешем ингридиентов")
    public ValidatableResponse passingInvalidHash(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .and()
                .contentType(ContentType.JSON)
                .body("{ \"ingredients\": [\"61c0c5a71d1f82001bda\"] }")
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }
    @Step("Проверка возврата ошибки системы при создании заказа с неверным хешем ингредиентов")
    public void errorCreate(ValidatableResponse invalidResponse) {
        invalidResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR); //При передаче невалидных данных, сервер не может упасть, подразумеваю должна быть ошибка 404, но в документации 500.
    }
    @Step("Get запрос на получение списка заказа")
    public ValidatableResponse getListOrder(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .and()
                .header("Content-type", "application/json")
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
    @Step("Проверка успешного получения списка заказов")
    public void getListSuccessful(ValidatableResponse listResponse) {
        listResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(1))
                .body("total", not(isEmptyOrNullString()))
                .body("totalToday", not(isEmptyOrNullString()));
    }
    @Step("Получение списка заказов без авторизации")
    public ValidatableResponse getListOrderUnauthorized() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
    @Step("Проверка ответа системы на запрос списка без авторизации")
    public void errorGetListOrder(ValidatableResponse listResponse) {
        listResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
