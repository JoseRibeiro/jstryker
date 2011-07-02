package app.repositories;

import javax.persistence.EntityManager;

import app.models.Product;

public class ProductRepositoryImpl
    extends Repository<Product, Long>
    implements ProductRepository {

	ProductRepositoryImpl(EntityManager entityManager) {
		super(entityManager);
	}
}
