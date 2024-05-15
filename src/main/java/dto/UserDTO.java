package dto;

import java.util.ArrayList;

public class UserDTO {

	private int id;
	private String name;
	private String email;
	private String status;
	private ArrayList<OrderDTO> orders;

	public UserDTO() {
		super();
	}

	public UserDTO(int id, String name, String email, String status) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.status = status;
	}

	public UserDTO(int id, String name, String email, String status, ArrayList<OrderDTO> orders) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.status = status;
		this.orders = orders;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getStatus() {
		return status;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<OrderDTO> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<OrderDTO> orders) {
		this.orders = orders;
	}

}
