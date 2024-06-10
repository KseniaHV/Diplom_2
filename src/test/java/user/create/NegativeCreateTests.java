package user.create;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserMethod;
public class NegativeCreateTests {
    private final UserMethod check = new UserMethod();
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
    @DisplayName("Негативный тест: создание пользователя, который уже зарегистрирован")
    public void testDuplicateUser() {
        User user = User.createdUser();
       ValidatableResponse createResponse = check.userRegistration(user);
        var response = check.successfulCreated(createResponse);

       ValidatableResponse duplicateResponse = check.userRegistration(user);
        check.creationError(duplicateResponse);
        
        accessToken = check.extractToken(response);
    }
    @Test
    @DisplayName("Негативный тест: создание пользователя без одного обязательного поля.")
    public void testMissingFields() {
        User withoutName = User.noRequiredField();

      ValidatableResponse errorResponse = check.userRegistration(withoutName);
        check.withoutName(errorResponse);

    }

}

