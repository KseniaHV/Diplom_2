package order.create;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import order.OrderMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserLogin;
import user.UserMethod;
import java.util.List;
public class PositiveCreateOrder {
    private final UserMethod check = new UserMethod();
    private final OrderMethod bill = new OrderMethod();
    private String accessToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @After
    public void deleteUser() {
        if (accessToken != null) {
            ValidatableResponse response = check.userDelete(accessToken);
            check.deleteSuccesfully(response);
        }
    }
    @Test
    @DisplayName("Позитивный тест: создание заказа с ингредиентами авторизованного пользователя")
    public void testCreateOrder() {
        User user = User.createdUser();
        var login = UserLogin.from(User.createdUser());

        check.userRegistration(user);   // Регистрация

        accessToken = check.userAuthorization(login); // Авторизация

        var ingredientsResponse = bill.getIngredientsResponse();    // Получение ингредиентов

        List<String> ingredientIds = bill.getIngredientIds(ingredientsResponse);  // Лист с ID ингредиентов

        List<String> selectedIngredientIds = bill.getFirstThreeIngredients(ingredientIds); // Получаем первые три ингредиента

        ValidatableResponse orderResponse = bill.createOrder(selectedIngredientIds, accessToken); // Создаем заказ

        bill.createSuccessful(orderResponse); // Проверяем успешное создание заказа
    }
}



