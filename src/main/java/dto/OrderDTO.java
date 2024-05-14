package dto;

public class OrderDTO {

	private int id;
	private int userId;
	private double totalAmount;
	private String status;

	public OrderDTO() {
		super();
	}

	public OrderDTO(int id, int userId, double totalAmount, String status) {
		super();
		this.id = id;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
