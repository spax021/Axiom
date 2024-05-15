package api.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.response.Response;

public class OrderTest extends BaseApiTest {

	private SoftAssert sa;

	@BeforeMethod
	public void setup() {
		startProcess("https://order-service");
		sa = new SoftAssert();
	}

	@AfterMethod
	public void tearDown() {
		sa.assertAll();
	}

	@Test(description = "Verify that an existing order can be retrieved with correct authentication and that the response contains the correct details.")
	public void retriveOrderByID() {
		Response response = retrieveOrderById(456);
		sa.assertEquals(response.statusCode(), 200);
		sa.assertEquals(order.getId(), 456);
		sa.assertTrue(order.getUserId() == 123);
		sa.assertTrue(order.getTotalAmount() > 0);
		sa.assertNotNull(order.getStatus());
	}
	
	//Boundry testing
	@Test(description = "Verify that attempting to retrieve an order with ID 0 returns an \"Order Not Found\" error.")
	public void retriveOrderByZero() {
		Response response = retrieveOrderById(0);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to retrieve an order with a negative ID returns an \"Order Not Found\" error.")
	public void retriveOrderByIdNegative() {
		Response response = retrieveOrderById(-1);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to retrieve an order with a non-existent ID (e.g., 123) returns an \"Order Not Found\" error.")
	public void retriveOrderByIDNonExistingId() {
		Response response = retrieveOrderById(123);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to retrieve an order with the wrong password returns an \"Authentication issue\" error.")
	public void retriveOrderByIdUnauthorised() {
		Response response = retrieveOrderByIdWithWrongCredentials(456);
		sa.assertEquals(response.statusCode(), 401);
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	//Security testing, retrieveOrderByIdWithoutAuth(123) request is not sending username and password
	@Test(description = "Verify that attempting to retrieve order details without authentication returns an \"Authentication issue\" error.")
	public void retriveOrderWithoutAuthentication() {
	    Response response = retrieveOrderByIdWithoutAuth(123);
	    sa.assertEquals(response.statusCode(), 401);
	    sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	@Test(description = "Verify that an order can be placed with valid data and that the response contains the correct details.")
	public void placeOrderSuccess() {
		Response response = placeOrder(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 201);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertTrue(order.getId() > 0);
		sa.assertEquals(order.getUserId(), 123);
		sa.assertEquals(order.getTotalAmount(), 576.23);
		sa.assertEquals(order.getStatus(), "pending");
	}

	@Test(description = "Verify that an order status can be updated to next stage >> shipped.")
	public void updateOrderStatus() {
		String status = "shipped";
		Response getResponse = retrieveOrderById(456);
		sa.assertEquals(getResponse.statusCode(), 200);
		sa.assertEquals(order.getUserId(), 123);
		
		order.setStatus(status);
		
		Response putResponse = updateOrder(order);
		sa.assertEquals(putResponse.statusCode(), 200);
		sa.assertEquals(newStatusFromResponse(putResponse),  "\"" + status + "\"");
	}
	
	//Stress testing
	@Test(description = "Perform a stress test by placing 1000 orders and verify that all creations are successful and performance is within acceptable limits.")
	public void stressTestPlacingOrders() {
		Response response;
		long startTime = System.currentTimeMillis();
		int successfulCreations = 0;
		
	    for (int i = 0; i < 1000; i++) {
	    	response = placeOrder(123, 576.23, "pending");
	    	if(response.statusCode() == 201) {
	    		successfulCreations++;
	    	}else {
	    		System.out.println("Failed placing order: " + i + " Response code: " + response.statusCode());
	    	}
	    }
	    
	    long endTime = System.currentTimeMillis();
	    long duration = endTime - startTime;
	    
	    sa.assertEquals(successfulCreations, 1000, "Some orders failed");
	    sa.assertTrue(duration < 60000, "Performance issue: Took longer than expected");  // just an example
	    System.out.println("Duration for 1000 requests: " + duration + "ms");
	}
	
	//Similar scenario like with users, mocking data from postman, intentionaly userId: 2 is returning error
	@Test(description = "Verify that attempting to place an order with the wrong password returns an \"Authentication issue\" error.")
	public void placeOrderWrongPassword() {
		Response response = placeOrderWithWrongPassword(2, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 401);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	@Test(description = "Verify that attempting to place an order without authentication returns an \"Authentication issue\" error.")
	public void placeOrderNoAuthentification() {
		Response response = placeOrderWithNoAuthentification(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 401);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to place an order with a status not defined by the system returns an \"Unknown status of order\" error.")
	public void placeOrderWithWrongStatus() {
		Response response = placeOrder(123, 576.23, "peeeeending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Unknown status of order", error.getErrorMessage()); 
	}
	
	//Pretty sure no one will ever place an order that is already shipped
	@Test(description = "Verify that attempting to place an order with a status that is not allowed (e.g., \"shipped\") returns an \"Invalid status of order\" error.")
	public void placeOrderWithStatusShipped() {
		Response response = placeOrder(123, 576.23, "shipped");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid status of order", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to place an order with a total amount of 0 returns an \"Invalid total amount value\" error.")
	public void placeOrderWithTotalAmountZero() {
		Response response = placeOrder(123, 0, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid total amount value", error.getErrorMessage());
	}
	
	@Test(description = "Verify that attempting to place an order with a negative total amount returns an \"Invalid total amount value\" error.")
	public void placeOrderWithTotalAmountNegative() {
		Response response = placeOrder(123, -576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid total amount value", error.getErrorMessage());
	}

	@Test(description = "Verify that attempting to place an order with a non-existent user ID returns a \"User id does not exist\" error.")
	public void placeOrderWithNonExistingUserId() {
		Response response = placeOrder(1, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("User id does not exist", error.getErrorMessage());
	}

	@Test(description = "Verify that attempting to place an order without a status returns an \"Invalid status of order\" error.")
	public void placeOrderWithMissingStatus() {
		Response response = placeOrder(1, 576.23, "");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid status of order", error.getErrorMessage()); // OR order must start with pending
	}

	@Test(description = "Verify that attempting to place an order with a malformed JSON request returns a \"Missing parameter: totalAmount\" error.")
	public void placeOrderMalformedJson() {
		Response response = placeOrderMalformedJson(1, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Missing parameter: totalAmount", error.getErrorMessage());
	}

	//Request body in placeOrderInvalidDataType() is modified
	@Test(description = "Verify that attempting to place an order with an invalid data type (e.g., userId as string) returns an \"Invalid data type provided\" error.")
	public void placeOrderWithInvalidDataType() {
		Response response = placeOrderInvalidDataType(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid data type provided", error.getErrorMessage());
	}
}
