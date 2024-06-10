package user.login;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import user.UserLogin;
import user.UserMethod;
public class NegativeLoginTests {
    private final UserMethod check = new UserMethod();
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("Негативный тест: логин с неверным логином и паролем.")
    public void testInvalidData() {
        UserLogin invalidUser = UserLogin.incorrectData();

     ValidatableResponse loginResponse = check.userLogin(invalidUser);
        check.invalidCredentials(loginResponse);
    }
}