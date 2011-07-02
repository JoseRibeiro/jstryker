package app.models;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@javax.persistence.Entity
public class Product {
	
	@Id @GeneratedValue
	private Long id;
	
	private String name;
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
