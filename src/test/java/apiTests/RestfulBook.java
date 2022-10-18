package apiTests;

import apiObjects.BookingApisObject;
import apiObjects.LoginApisObject;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
public class RestfulBook extends Base {
    String loginToken;
    int bookingID;
    BookingApisObject bookingApisObject;
    // Login (Create token one time in a class as precondition)
    @BeforeClass
    public void setLoginToken() {
        LoginApisObject loginApisObject = new LoginApisObject(requestSpecification);
        loginToken = loginApisObject.loginToApp();
        System.out.println("TOKEN:" + loginToken);
        bookingApisObject = new BookingApisObject(requestSpecification, loginToken);
    }
    //Create booking in test method with the suitable assertions
    @Test(priority = 0)
    public void testCreateValidBooking() {
        var validatableResponse =  bookingApisObject.createBooking();
        validatableResponse.body("booking.firstname", equalTo("Jim"));
        validatableResponse.statusCode(200);
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        bookingID = jsonPath.getInt("bookingid");
        validatableResponse.log().all();
    }
//Edit the booking created
@Test(priority = 1, dependsOnMethods = "testCreateValidBooking")
public void testEditeBooking() {
    ValidatableResponse validatableResponse= bookingApisObject.editBooking(bookingID, loginToken);
    validatableResponse.header("Content-Type", "application/json; charset=utf-8");
    validatableResponse.body("firstname", equalTo("Ali"));
    validatableResponse.statusCode(200);
}
// Get the booking
@Test(priority = 2, dependsOnMethods = "testEditeBooking")
public void testGetBooking() {
    var validatableResponse = bookingApisObject.getBooking(bookingID);
    validatableResponse.body("firstname", equalTo("Ali"));

    validatableResponse.statusCode(200);
}
// Delete the booking created
@Test(priority = 3, dependsOnMethods = "testGetBooking")
public void testDeleteBooking() {
    var validatableResponse = bookingApisObject.deleteBooking(bookingID, loginToken);
    Response response = validatableResponse.extract().response();
    JsonPath jsonPath = response.jsonPath();
    validatableResponse.statusCode(201);
}

}
