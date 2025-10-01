package com.picpaysimplificado.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
	
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EntityManager entityManager;
	
	@Test
	@DisplayName("Should get User sucessfully from DB")
	void findUserByDocumentCase1() {
		String document = "99999999901";
		UserDTO userDTO = new UserDTO("Fernanda",
				"Teste",
				document,
				new BigDecimal(10),
				"test@gmail.com",
				"44444",
				UserType.COMMON);
		this.createUser(userDTO);
		
		Optional<User> result = this.userRepository.findByDocument(document);
		
		assertThat(result.isPresent()).isTrue();
	}
	
	
	@Test
	@DisplayName("Should not get User from DB when user not exits")
	void findUserByDocumentCase2() {
		String document = "99999999901";
				
		Optional<User> result = this.userRepository.findByDocument(document);
		
		assertThat(result.isEmpty()).isTrue();
	}
	
	
	
	private User createUser(UserDTO userDTO) {
		User newUser = new User(userDTO);
		this.entityManager.persist(newUser);
		return newUser;
		
	}
	

}
