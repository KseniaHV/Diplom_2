package order.get;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import order.OrderMethod;
import org.junit.Before;
import org.junit.Test;
public class NegativeGetOrderTests {
    private final OrderMethod bill = new OrderMethod();
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("Негативный тест: получение заказа неавторизованного пользователя")
    public void testGetUserOrdersWithoutAuth() {
      ValidatableResponse listResponse = bill.getListOrderUnauthorized();
        bill.errorGetListOrder(listResponse);
    }
}

