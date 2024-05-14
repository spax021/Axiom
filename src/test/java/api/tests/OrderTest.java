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
//		startProcess("https://order-service");
		startProcess("https://199aa9ac-7341-4f36-b709-674b02fb78d4.mock.pstmn.io");
		sa = new SoftAssert();
	}

	@AfterMethod
	public void tearDown() {
		sa.assertAll();
	}

	@Test(description = "Order with ID 456, existing id, correct auth, details verification in response")
	public void retriveOrderByID() {
		Response response = retrieveOrderById(456);
		sa.assertEquals(response.statusCode(), 200);
		sa.assertEquals(order.getId(), 456);
		sa.assertTrue(order.getUserId() == 123);
		sa.assertTrue(order.getTotalAmount() > 0);
		sa.assertNotNull(order.getStatus());
	}
	
	//Boundry testing
	@Test(description = "Order ID is 0")
	public void retriveOrderByZero() {
		Response response = retrieveOrderById(0);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Order ID is negative")
	public void retriveOrderByIdNegative() {
		Response response = retrieveOrderById(-1);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Order ID is 123, positive but non existing")
	public void retriveOrderByIDNonExistingId() {
		Response response = retrieveOrderById(123);
		sa.assertEquals(response.statusCode(), 404);
		sa.assertEquals("Order Not Found", error.getErrorMessage());
	}
	
	@Test(description = "Accessing order with wrong password")
	public void retriveOrderByIdUnauthorised() {
		Response response = retrieveOrderByIdWithWrongCredentials(456);
		sa.assertEquals(response.statusCode(), 401);
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	//Security testing, in request we are not sending username and password
	@Test(description = "Access order details without authentication")
	public void retriveOrderWithoutAuthentication() {
	    Response response = retrieveOrderByIdWithoutAuth(123);
	    sa.assertEquals(response.statusCode(), 401);
	    sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	@Test(description = "Place order with valid data")
	public void placeOrderSuccess() {
		Response response = placeOrder(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 201);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertTrue(order.getId() > 0);
		sa.assertEquals(order.getUserId(), 123);
		sa.assertEquals(order.getTotalAmount(), 576.23);
		sa.assertEquals(order.getStatus(), "pending");
	}

	//Stress testing
	@Test(description = "Stress test - multiple order creations")
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
	
	//Similar scenario like with users, mocking data
	@Test(description = "Place order with wrong password")
	public void placeOrderWrongPassword() {
		Response response = placeOrderWithWrongPassword(2, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 401);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}

	@Test(description = "Place order with no auth")
	public void placeOrderNoAuthentification() {
		Response response = placeOrderWithNoAuthentification(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 401);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Authentication issue", error.getErrorMessage());
	}
	
	@Test(description = "Place order with status not defined by the system")
	public void placeOrderWithWrongStatus() {
		Response response = placeOrder(123, 576.23, "peeeeending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Unknown status of order", error.getErrorMessage()); 
	}
	
	//Pretty sure no one will ever place an order that is already shipped
	@Test(description = "Place order with status not defined by the system")
	public void placeOrderWithStatusShipped() {
		Response response = placeOrder(123, 576.23, "shipped");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid status of order", error.getErrorMessage());
	}
	
	@Test(description = "Place order with total amount 0")
	public void placeOrderWithTotalAmountZero() {
		Response response = placeOrder(123, 0, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid total amount value", error.getErrorMessage());
	}
	
	@Test(description = "Place order with total amount negative")
	public void placeOrderWithTotalAmountNegative() {
		Response response = placeOrder(123, -576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid total amount value", error.getErrorMessage());
	}

	@Test(description = "Place order with non existing userID")
	public void placeOrderWithNonExistingUserId() {
		Response response = placeOrder(1, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("User id does not exist", error.getErrorMessage());
	}

	@Test(description = "Place order with missing status")
	public void placeOrderWithMissingStatus() {
		Response response = placeOrder(1, 576.23, "");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid status of order", error.getErrorMessage()); // OR order must start with pending
	}

	@Test(description = "Malformed JSON Order request")
	public void placeOrderMalformedJson() {
		Response response = placeOrderMalformedJson(1, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Missing parameter: totalAmount", error.getErrorMessage());
	}

	//Request body is modified
	@Test(description = "Place order with invalid data type / userId as string")
	public void placeOrderWithInvalidDataType() {
		Response response = placeOrderInvalidDataType(123, 576.23, "pending");
		sa.assertEquals(response.statusCode(), 400);
		sa.assertTrue(!response.asPrettyString().isEmpty());
		sa.assertEquals("Invalid data type provided", error.getErrorMessage());
	}
}
