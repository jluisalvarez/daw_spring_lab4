# JPA y MySQL con Spring Framework

## Requisitos

- Java 21
- IDE: Visual Code

## Aplicación

```
                                  '------ Modelo -----'
Vista  <--->  Controlador  <--->  Servicio  <--->  DAO  <---> DB
```

## Crea un proyecto desde Spring Boot Initializr

http://start.spring.io/

Completa el asistente, seleccionando:

- Gestor Proyecto: Maven
- Lenguaje: Java
- Versión de Spring Boot: 3.2.0
- Project Metadata
    - Group: daw.example
    - Artifact: jpamysql
    - Name: jpamysql
    - Description: HelloWorld project for Spring Boot
    - Package name: daw.example.jpamysql
    - Packaging: Jar
    - Java: 21
- Dependencias
    - Spring Boot DevTools
	- Lombok
    - Spring Web
    - Thymeleaf
    - MySQL Driver
    - Spring Data JPA

Genera el contenido, descárgalo y descomprime. Utiliza el IDE para abrir el proyecto.

## Configuración conexión base de datos: application.properties

https://spring.io/guides/gs/accessing-data-mysql/

```
spring.datasource.url=jdbc:mysql://localhost:3306/prueba_app
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
#spring.jpa.properties.hibernate.format_sql=true
#spring.jpa.show-sql: true
```

## Crear una entidad

Crea el directorio models en /src/main/java/daw/examples/jpamysql y crea una clase User.java con el siguiente contenido:

```java
package daw.examples.jpamysql.models;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="usuarios")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    
	private String nombre;
	private String email;
	
}
```

## Crea una interface de acceso a datos: DAO - Data Access Object

Crea el directorio dao en /src/main/java/daw/examples/jpamysql y crea una interface IUserDAO.java con el siguiente contenido:

```java
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
```

## Crea una interface y una implementación de servicio para manejo del DAO

Crea el directorio services en /src/main/java/daw/examples/jpamysql y crea una interface IUserService.java con el siguiente contenido:

```java
package daw.examples.jpamysql.services;

import java.util.List;
import daw.examples.jpamysql.models.User;

public interface IUserService {

	public List<User> listarUsuarios();
	
	public void guardarUsuario(User user);
	
	public void eliminarUsuario(User user);
	
	public User buscarUsuario(User user);
	
}
```

Crea, en el mismo directorio, el fichero UserServices.java con la implementación de la interface:

```java
package daw.examples.jpamysql.services;

import java.util.List;
import daw.examples.jpamysql.dao.IUserDAO;
import  daw.examples.jpamysql.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
    
@Service
public class UserService implements IUserService {
    
    @Autowired
    private IUserDAO usuarioDao;
    
    @Override
    @Transactional(readOnly = true)
    public List<User> listarUsuarios() {
        List<User> listUsers = (List<User>) usuarioDao.findAll();
        return listUsers;
    }

    @Override
    @Transactional
    public void guardarUsuario(User usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(User usuario) {
        usuarioDao.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public User buscarUsuario(User usuario) {
        return usuarioDao.findById(usuario.getId()).orElse(null);
    }
}
```

## Crear controlador

Crea el directorio controllers en /src/main/java/daw/examples/jpamysql y crea la clase UserController.java con el siguiente contenido:

```java
package daw.examples.jpamysql.controllers;

import java.util.List;

import daw.examples.jpamysql.models.User;
import daw.examples.jpamysql.services.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {

	@Autowired
	private IUserService userService;

	@GetMapping("/")
	public String index(Model model) {
	    
	    log.info("Ejecutando método index en controlador UserController");
	    
	    List<User> usuarios = userService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
	    
		return "index";
	}
	
	@GetMapping("/add")
	public String add(User u) {
		log.info("Ejecutando método add en controlador UserController");
		return "formUser";
	}
	
	@PostMapping("/save")
	public String save(User u) {
		log.info("Ejecutando método save en controlador UserController");
		log.info("Usuario " + u);
		userService.guardarUsuario(u);
		
		return "redirect:/";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(User u, Model model) {
		log.info("Ejecutando método edit en controlador USerController");
		log.info("Editando usuario " + u);
		User usuario = userService.buscarUsuario(u);
		
		model.addAttribute("user", usuario);
		return "formUser";
	}
	
	@GetMapping("/delete")
	public String delete(User u){
		log.info("Ejecutando método delete en controlador UserController");
		userService.eliminarUsuario(u);
		return "redirect:/";
	}
	
	
}
```

## Crea la vistas

En la carpeta /src/main/resources/template:

- Crea la vista index.html con el siguiente contenido:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

    <head>
        <title>Index</title>
    </head>
    
    <body>
        <h1>Inicio v.1</h1>
        <p th:text="${mensaje}"></p>

        <p><a th:href="@{/add}">Añadir Usuario</a></p>
       
       <table th:if="${usuarios!=null and !usuarios.empty}">
    	<tr>
    	  <th>Nombre</th>
    	  <th>Correo</th>
    	  <th>Acciones</th>
    	</tr>
    	<tr th:each="usuario : ${usuarios}">
          <td th:text="${usuario.nombre}">Nombre Persona</td>
    	  <td th:text="${usuario.email}">Correo Persona</td>
    	  <td>
    	      <a th:href="@{/edit/} + ${usuario.id}" th:text="Editar">Editar</a> | 
    	      <a th:href="@{/delete(id=${usuario.id})}" th:text="Eliminar">Eliminar</a>
    	  </td>
    	</tr>
    	</table>
    	<p th:if="${usuarios==null or usuarios.empty}">No hay usuarios</p>	
	
	</body>
</html>
```

- Crea la vista formUser.html con el siguiente contenido:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

    <head>
        <title>Usuarios</title>
    </head>
    
    <body>
        <h1>Añadir usuario</h1>
        
        <form th:action="@{/save}" method="post" th:object="${user}">
            <input type="hidden" name="id"  th:field="*{id}" />            
            <p>Nombre: <input type="text" name="nombre" th:field="*{nombre}" /></p>
	        <p>Correo: <input type="email" name="email" th:field="*{email}" /></p>
	        <p><input type="submit" value="Enviar" /></p>
        </form>


        <p><a th:href="@{/}">Volver</a></p>
	
	</body>
</html>
```