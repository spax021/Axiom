package api.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.response.Response;

public class UserTest extends BaseApiTest {

	private SoftAssert sa;
	
	@BeforeMethod
	public void setup() {
		startProcess("https://user-service");
		sa = new SoftAssert();
	}

	@AfterMethod
	public void tearDown() {
		sa.assertAll();
	}

	//Happy path scenario
	@Test(description = "Verify that existing can be retrieved with correct authentication and that the response contains the correct details.")
	public void accessUserByIdSuccess() {
		Response response = getUserById(123);
		sa.assertEquals(response.statusCode(), 200);
		sa.assertEquals(user.getId(), 123);
		sa.assertEquals(user.getName(), "John Doe");
		sa.assertEquals(user.getEmail(), "john.doe@example.com");
		sa.assertEquals(user.getStatus(), "active");
	}
	
	@Test(description = "Verify that the email of a existing user can be successfully updated.")
	public void updateUserEmail() {
		String newEmail = "jo.do@example.com";
		Response getResponse = getUserById(123);
		sa.assertEquals(getResponse.statusCode(), 200);
		sa.assertEquals(user.getId(), 123);
		
		user.setEmail(newEmail);
		
		Response putResponse = updateUser(user);
		sa.assertEquals(putResponse.statusCode(), 200);
		sa.assertEquals(newEmailFromResponse(putResponse), "\"" + newEmail + "\"");
	}

	@Test(description = "Verify that the status of a existing user can be successfully updated to inactive.")
	public void updateUserStatus() {
		String newStatus = "inactive";
		Response getResponse = getUserById(123);
		sa.assertEquals(getResponse.statusCode(), 200);
		
		System.out.println(getResponse.asPrettyString());
		
		sa.assertEquals(user.getId(), 123);
		sa.assertTrue(!getResponse.asPrettyString().isEmpty());
		
		user.setEmail(newStatus);
		
		Response putResponse = updateUser(user);
		sa.assertEquals(putResponse.statusCode(), 200);
		sa.assertEquals(newStatusFromResponse(putResponse), "\"" + newStatus + "\"");
		System.out.println(putResponse.asPrettyString());
	}
	
	//Boundry testing
	@Test(description = "Verify that attempting to retrieve a user with id 0 returns a \"User Not Found\" error.")
	public void accessUserByIdZero() {
	    Response response = getUserById(0);
	    sa.assertEquals(response.statusCode(), 404);
	    sa.assertEquals("User Not Found", error.getErrorMessage()); // OR userId must be greater than 0
	}
	
	@Test(description = "Verify that attempting to retrieve a user with a negative id returns a \"User Not Found\" error.")
	public void accessUserByIdNegative() {
	    Response response = getUserById(-1);
	    sa.assertEquals(response.statusCode(), 404);
	    sa.assertEquals("User Not Found", error.getErrorMessage()); // OR userId must be greater than 0
	}
	
	//This test is searching for user with positive ID but not found
	@Test(description = "Verify that attempting to retrieve a user with a non-existent id (e.g., 1) returns a \"User Not Found\" error.")
	public void accessUserByIdNotFound() {
		Response response = getUserById(1);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("User Not Found", error.getErrorMessage());
	}
	
	//Here user is entering wrong password
	@Test(description = "Verify that attempting to retrieve a user with the wrong password returns an \"Authentication issue\" error.")
	public void accessByIdUnauthorised() {
		Response response = getUserByIdWithWrongCredentials(2);
		sa.assertEquals(response.statusCode(), 401);
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}
	
	//Security testing, in request we are not sending username and password
	@Test(description = "Verify that attempting to retrieve user details without authentication returns an \"Authentication issue\" error.")
	public void accessUserWithoutAuthentication() {
	    Response response = getUserByIdWithoutAuth(123);
	    sa.assertEquals(response.statusCode(), 401);
	    sa.assertEquals("Authentication issue", error.getErrorMessage());
	}
	
	@Test(description = "Verify that a user can be created with valid data and that the response contains the correct details.")
	public void createUserSuccess() {
		Response response = createUser("John Doe", "john.doe@example.com", "active");
		sa.assertEquals(response.statusCode(), 201);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("John Doe", user.getName());
		sa.assertEquals("john.doe@example.com", user.getEmail());
		sa.assertEquals("active", user.getStatus());
		}
	
	//Stress testing
	@Test(description = "Perform a stress test by creating 1000 users and verify that all creations are successful and performance is within acceptable limits.")
	public void stressTestUserCreation() {
		Response response;
		long startTime = System.currentTimeMillis();
		int successfulCreations = 0;
		
	    for (int i = 0; i < 1000; i++) {
	    	response = createUser("John Doe" + i, "john.doe" + i + "@example.com", "active");
	    	if(response.statusCode() == 201) {
	    		successfulCreations++;
	    	}else {
	    		System.out.println("Failed creation for User" + i + ": " + response.statusCode());
	    	}
	    }
	    
	    long endTime = System.currentTimeMillis();
	    long duration = endTime - startTime;
	    
	    sa.assertEquals(successfulCreations, 1000, "Some user creation failed");
	    sa.assertTrue(duration < 60000, "Performance issue: Took longer than expected");  // just an example
	    System.out.println("Duration for 1000 requests: " + duration + "ms");
	}

	//I am mocking data from postman for easier writing of tests
	//when I put inactive as status, it is simulating "wrong" password to get correct response
	@Test(description = "Verify that attempting to create a user with the wrong password returns an \"Authentication issue\" error.")
	public void createUserUnauthorised() {
		Response response = createUserWithWrongPassword("John Doe", "john.doe@example.com", "inactive");
		sa.assertEquals(response.statusCode(), 401);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}
	
	//Localisation
	//We want to make sure that system is handling different character sets if needed
	@Test(description = "Verify that a user can be created with international characters in the name.")
	public void createUserWithInternationalCharacters() {
	    Response response = createUser("李四", "li.si@example.com", "active");
	    sa.assertEquals(response.statusCode(), 201);
	    sa.assertEquals(response.jsonPath().getString("name"), "李四");
	}
	
	@Test(description = "Verify that attempting to create a user with an invalid email format returns an \"Invalid request body: email\" error.")
	public void createUserWithInvalidEmail() {
	    Response response = createUser("John Doe", "john.doe@", "active");
	    sa.assertEquals(response.statusCode(), 400);
	    sa.assertEquals("Invalid request body: email", error.getErrorMessage());
	}

	@Test(description = "Verify that attempting to create a user without a name returns an \"Invalid request body: name\" error.")
	public void createUserWithoutName() {
		Response response = createUser("", "john.doe@example.com", "active");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid request body: name", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to create a user using an invalid endpoint returns an \"Endpoint not found\" error.")
	public void invalidEndpointTest() {
	    Response response = invalidEndpointCall("John Doe", "john.doe@example.com", "active");
	    sa.assertEquals(response.statusCode(), 404);
	    sa.assertEquals("Endpoint not found", error.getErrorMessage());
	}

	@Test(description = "Verify that attempting to create a user with a malformed JSON request returns a \"Malformed JSON\" or \"Bad Request\" error.")
	public void malformedJsonRequest() {
	    Response response = createUserMalformedJson("John Doe", "john.doe@example.com", "active");
	    sa.assertEquals(response.statusCode(), 400);
	    sa.assertEquals("Malformed JSON", error.getErrorMessage()); // Or Bad Request message
	}

	//Precondition for this test would be placeOrderSuccess() test
	@Test(description = "Verify that the orders of a existing user can be successfully retrieved and contain the correct details.")
	public void getUserOrdersByUserId() {
		Response response = getUserOrders(123);
		sa.assertEquals(response.statusCode(), 200);
		for (int i = 0; i < orders.size(); i++) {
			sa.assertTrue(orders.get(i).getUserId() == 123);
			sa.assertTrue(orders.get(i).getTotalAmount() > 0);
			sa.assertNotNull(orders.get(i).getStatus()); // OR create logic to assert all available statuses defined by the system
		}
	}

	@Test(description = "Verify that attempting to retrieve orders for a non-existent user returns a \"User Not Found\" error.")
	public void getNonExistingUserOrdersByUserId() {
		Response response = getUserOrders(2);
		System.out.println(response.asPrettyString());
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("User Not Found", error.getErrorMessage());
	}
	
	
}
