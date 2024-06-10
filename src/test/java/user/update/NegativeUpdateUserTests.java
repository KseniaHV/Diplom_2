package user.update;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserMethod;
public class NegativeUpdateUserTests {
    private final UserMethod check = new UserMethod();
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("Негативный тест: изменение данных пользователя без авторизации")
    public void updateUserWithoutAuth() {
        User updateUser = User.updatedDada();
      ValidatableResponse errorResponse = check.updateDadaWithoutAuth(updateUser);
        check.errorAuthorization(errorResponse);
    }
}

