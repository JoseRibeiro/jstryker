package striker.domain;

public class OnlyGet {
	
	private static String status;
	
	private String name;
	
	private String description = "anyValue";
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public static String getStatus() {
		return status;
	}
}