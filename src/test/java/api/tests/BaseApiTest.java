package api.tests;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import config.PropertiesFile;
import dto.ErrorDTO;
import dto.OrderDTO;
import dto.UserDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseApiTest {

	private static String username = PropertiesFile.getUsername();
	private static String password = PropertiesFile.getPassword();
	private static String incorectPassword = PropertiesFile.getIncorectPassword();
	
	protected static UserDTO user;
	protected static OrderDTO order;
	protected static List<OrderDTO> orders;
	protected static ErrorDTO error;

	private static RequestSpecification request;

	public BaseApiTest() {
	}

	protected static void startProcess(String baseURI) {
		RestAssured.baseURI = baseURI;
		request = RestAssured.given().header("Content-Type", "application/json");
	}

	public static Response getUserById(int id) {
		Response response = given()
				.auth()
				.basic(username, password)
				.when()
				.get("/users/" + Integer.toString(id));
		populateDto(response, "user");
		return response;
	}

	public static Response updateUser(UserDTO user) {
		String requestBody = "{\r\n" 
				+ "    \"name\": \"" + user.getName() + "\",\r\n" 
				+ "    \"email\": \"" + user.getEmail() + "\",\r\n"
				+ "    \"status\": \"" + user.getStatus() + "\"\r\n" + "}";

	Response response = given()
			.auth()
			.basic(username, password)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.put("/users/" + Integer.toString(user.getId()));
		return response;
	}
	
	public static String newEmailFromResponse(Response response) {
		ObjectMapper obj = new ObjectMapper();
		String email = "";
		try {
			JsonNode root = obj.readTree(response.asPrettyString());
			email = root.get("email").toString();
		} catch (Exception e) { e.printStackTrace();}
		
		return email;
	}

	public static String newStatusFromResponse(Response response) {
		ObjectMapper obj = new ObjectMapper();
		String status = "";
		try {
			JsonNode root = obj.readTree(response.asPrettyString());
			status = root.get("status").toString();
		} catch (Exception e) { e.printStackTrace();}
		
		return status;
	}

	public static Response getUserByIdWithWrongCredentials(int id) {
		Response response = given()
				.auth()
				.basic(username, incorectPassword)
				.when()
				.get("/users/" + Integer.toString(id));
		populateDto(response, "");
		return response;
	}

	public static Response getUserByIdWithoutAuth(int id) {
		Response response = given()
				.when()
				.get("/users/" + Integer.toString(id));
		populateDto(response, "");
		return response;
	}
	

	public static Response invalidEndpointCall(String name, String email, String status) {
		String requestBody = "{\r\n" 
					+ "    \"name\": \"" + name + "\",\r\n" 
					+ "    \"email\": \"" + email + "\",\r\n"
					+ "    \"status\": \"" + status + "\"\r\n" + "}";

		Response response = given()
				.auth()
				.basic(username, password)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/us"); //us(ers) is missing from url
		
		populateDto(response, name, email, status);
		return response;
	}
	

	public static Response createUserMalformedJson(String name, String email, String status) {
		String requestBody = "{\r\n" 
					+ "    \"name\": \"" + name + "}";

		Response response = given()
				.auth()
				.basic(username, password)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/users");
		
		populateDto(response, name, email, status);
		return response;
	}

	public static Response createUser(String name, String email, String status) {
		String requestBody = "{\r\n" 
					+ "    \"name\": \"" + name + "\",\r\n" 
					+ "    \"email\": \"" + email + "\",\r\n"
					+ "    \"status\": \"" + status + "\"\r\n" + "}";

		Response response = given()
				.auth()
				.basic(username, password)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/users");
		
		populateDto(response, name, email, status);
		return response;
	}

	public static Response createUserWithWrongPassword(String name, String email, String status) {
		String requestBody = "{\r\n" 
					+ "    \"name\": \"" + name + "\",\r\n" 
					+ "    \"email\": \"" + email + "\",\r\n"
					+ "    \"status\": \"" + status + "\"\r\n" + "}";

		Response response = given()
				.auth()
				.basic(username, incorectPassword)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/users");
		
		populateDto(response, name, email, status);
		return response;
	}

	public static Response getUserOrders(int id) {
		Response response = given()
				.auth()
				.basic(username, password)
				.when()
				.get("/users/" + Integer.toString(id) + "/orders");
		populateDto(response, "order");
		return response;
	}	
	
	public static Response retrieveOrderById(int id) {
		Response response = given()
				.auth()
				.basic(username, password)
				.when()
				.get("/orders/" + Integer.toString(id));
		populateDto(response, "orderById");
		return response;
	}
	
	public static Response retrieveOrderByIdWithWrongCredentials(int id) {
		Response response = given()
				.auth()
				.basic(username, incorectPassword)
				.when()
				.get("/orders/" + Integer.toString(id));
		populateDto(response, "orderById");
		return response;
	}
	
	public static Response retrieveOrderByIdWithoutAuth(int id) {
		Response response = given()
				.when()
				.get("/orders/" + Integer.toString(id));
		populateDto(response, "orderById");
		return response;
	}

	public static Response placeOrder(int userId, double totalAmount, String status) {
		String requestBody = "{\r\n" 
				+ "    \"userId\": \"" + userId + "\",\r\n" 
				+ "    \"totalAmount\": \"" + totalAmount + "\",\r\n"
				+ "    \"status\": \"" + status + "\"\r\n" + "}";

	Response response = given()
			.auth()
			.basic(username, password)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post("/orders");
	
	populateDto(response, totalAmount, status);
	return response;
	}
	
	public static Response placeOrderWithWrongPassword(int userId, double totalAmount, String status) {
		String requestBody = "{\r\n" 
				+ "    \"userId\": \"" + userId + "\",\r\n" 
				+ "    \"totalAmount\": \"" + totalAmount + "\",\r\n"
				+ "    \"status\": \"" + status + "\"\r\n" + "}";

	Response response = given()
			.auth()
			.basic(username, incorectPassword)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post("/orders");
	
	populateDto(response, totalAmount, status);
	return response;
	}
	
	public static Response placeOrderWithNoAuthentification(int userId, double totalAmount, String status) {
		String requestBody = "{\r\n" 
				+ "    \"userId\": \"" + userId + "\",\r\n" 
				+ "    \"totalAmount\": \"" + totalAmount + "\",\r\n"
				+ "    \"status\": \"" + status + "\"\r\n" + "}";

	Response response = given()
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post("/orders");
	
	populateDto(response, totalAmount, status);
	return response;
	}
	
	public static Response placeOrderMalformedJson(int userId, String status) {
		String requestBody = "{\r\n" 
				+ "    \"userId\": \"" + userId + "\",\r\n" 
				
				+ "    \"status\": \"" + status + "\"\r\n" + "}";

	Response response = given()
			.auth()
			.basic(username, password)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post("/orders");
	
//	populateDto(response, status);
	return response;
	}

	public static Response placeOrderInvalidDataType(int userId, double totalAmount, String status) {
		String requestBody = "{\r\n"
				+ "    \"userId\": \"" + userId + "\",\r\n" //here in requestBody I am sending it as a string
				+ "    \"totalAmount\": \"" + totalAmount + "\",\r\n"
				+ "    \"status\": \"" + status + "\"\r\n" + "}";

	Response response = given()
			.auth()
			.basic(username, password)
			.contentType(ContentType.JSON)
			.body(requestBody)
			.when()
			.post("/orders");
	
	populateDto(response, totalAmount, status);
	return response;
	}
	
	
	
	
	
	private static void populateDto(Response response, String type) {
		ObjectMapper obj = new ObjectMapper();

		if (type.equals("user")) {
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				try {
					user = obj.readValue(response.asPrettyString(), UserDTO.class);
				} catch (Exception e) { e.printStackTrace(); }
			} else {
				try {
					error = obj.readValue(response.asPrettyString(), ErrorDTO.class);
				} catch (Exception e) { e.printStackTrace(); }
			}
		} else if (type.equals("order")){
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				try {
					JsonNode root = obj.readTree(response.asPrettyString());
					String order = root.get("orders").toString();
					
					orders = obj.readValue(order, new TypeReference<List<OrderDTO>>() {});
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {
					error = obj.readValue(response.asPrettyString(), ErrorDTO.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (type.equals("orderById")){
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				try {
					
					order = obj.readValue(response.asPrettyString(), OrderDTO.class);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {
					error = obj.readValue(response.asPrettyString(), ErrorDTO.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	// Response in assignment example is returning only new ID and userID, from that response I will populate my DTO > populateDto(response, "orderById");
	// Manualy using setters I will populate rest of the values
	private static void populateDto(Response response, double totalAmount, String status) {
		if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			populateDto(response, "orderById");
			order.setTotalAmount(totalAmount);
			order.setStatus(status);
		} else {
			populateDto(response, "error");
		}
	}
	
	// Assignment example is returning only ID of new user, from response I will populate my DTO with ID > populateDto(response, "user");
	// Manualy using setters I will populate rest of the values
	private static void populateDto(Response response, String name, String email, String status) {
		
		if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			populateDto(response, "user");
			user.setName(name);
			user.setEmail(email);
			user.setStatus(status);
		} else {
			populateDto(response, "error");
		}
		
	}
	
	private void createMockData() {
			
		OrderDTO order1 = new OrderDTO(1, 10, 100, "pending");
		OrderDTO order2 = new OrderDTO(2, 10, 100, "shipped");
		OrderDTO order3 = new OrderDTO(3, 10, 100, "delivered");
		OrderDTO order4 = new OrderDTO(4, 20, 100, "pending");
		OrderDTO order5 = new OrderDTO(5, 20, 100, "shipped");
		OrderDTO order6 = new OrderDTO(6, 10, 100, "pending");
		OrderDTO order7 = new OrderDTO(7, 10, 100, "delivered");
		
		ArrayList<OrderDTO> orders1 = new ArrayList<>();
		orders1.add(order1);
		orders1.add(order2);
		orders1.add(order3);
		ArrayList<OrderDTO> orders2 = new ArrayList<>();
		orders2.add(order4);
		orders2.add(order5);
		ArrayList<OrderDTO> orders3 = new ArrayList<>();
		orders3.add(order6);
		orders3.add(order7);
		
		UserDTO user1 = new UserDTO(10, "Petar Petrovic", "pepe@example.com", "active", orders1);
		UserDTO user2 = new UserDTO(20, "Stevan Stevanovic", "stst@example.com", "active", orders2);
		UserDTO user3 = new UserDTO(30, "Milan Milanovic", "mimi@example.com", "inactive", orders3);

	}
}
