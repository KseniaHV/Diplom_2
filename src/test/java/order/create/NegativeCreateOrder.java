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
public class NegativeCreateOrder {
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
    @DisplayName("Негативный тест: создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        var ingredientsResponse = bill.getIngredientsResponse(); // Получем список ингридиентов

        List<String> ingredientIds = bill.getIngredientIds(ingredientsResponse);  // Лист с ID ингредиентов
       
        List<String> selectedIngredientIds = bill.getFirstThreeIngredients(ingredientIds); // Получаем первые три ингредиента
        
       ValidatableResponse orderResponse = bill.createOrderWithoutAuth(selectedIngredientIds);  //Создаем заказ
        bill.createSuccessful(orderResponse); // Проверяем успешное создание заказа
    }
    @Test
    @DisplayName("Негативный тест: создание заказа без ингредиентов")
    public void testWithoutIngredients() {
        User user = User.createdUser();
        var login = UserLogin.from(User.createdUser());
       
        check.userRegistration(user);
        accessToken = check.userAuthorization(login);

     ValidatableResponse noIngredientsResponse = bill.createOrderWithoutIngredients(accessToken); // Создание заказа без ингредиентов
        bill.errorCreateOrder(noIngredientsResponse);
    }
    @Test
    @DisplayName("Негативный тест: создание заказа с неверным хешем ингредиентов")
    public void testWithInvalidIngredient() {
        User user = User.createdUser();
        var login = UserLogin.from(User.createdUser());
        
        check.userRegistration(user);
        accessToken = check.userAuthorization(login);

       ValidatableResponse invalidResponse = bill.passingInvalidHash(accessToken); // Создание заказа с не валидным хэш
        bill.errorCreate(invalidResponse);
    }
}

