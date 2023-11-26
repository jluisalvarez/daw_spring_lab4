package daw.examples.jpamysql.dao;

import daw.examples.jpamysql.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserDAO extends JpaRepository<User, Long>{

	// ya temdriamos los prinicpales métodos CRUD
	/*
	 * https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html
	 */
	// y podemos crear nuestros propios métodos
	/* https://www.baeldung.com/spring-data-derived-queries */

}
