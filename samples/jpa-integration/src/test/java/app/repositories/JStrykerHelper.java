package app.repositories;

import org.jstryker.database.JPAHelper;

//this class should be part of core

public class JStrykerHelper {
	
	private static boolean load;
	
	//this is useful to use hibernate to create tables
	public static void init() {
		if (!load) {
			load = true;
			JPAHelper.entityManagerFactory("default");
		}
	}
}
