package dto;

public class ErrorDTO {

	private String errorMessage;

	public ErrorDTO() {
		super();
	}

	public ErrorDTO(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
