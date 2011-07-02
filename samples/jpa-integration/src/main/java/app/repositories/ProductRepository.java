package app.repositories;

import java.util.List;

import app.models.Product;

public interface ProductRepository {
	 
	void create(Product entity);
	
	Product update(Product entity);
	
	void destroy(Product entity);
	
	Product find(Long id);
	
	List<Product> findAll();
}
