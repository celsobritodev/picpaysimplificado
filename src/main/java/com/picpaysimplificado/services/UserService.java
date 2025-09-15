package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public void validateTransaction(User sender, BigDecimal amount) {
		if (sender.getUserType() == UserType.MERCHANT) {
			throw new IllegalArgumentException("Usuario do tipo lojista não pode realizar transação");
		}

		if (sender.getBalance().compareTo(amount) < 0) {
			throw new IllegalArgumentException("Saldo insuficiente para realizar a transação");
		}

	}

	public User findUserById(Long id) {
		
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
	}
	
	
	public User createUser(UserDTO dataDto) {
		User newUser = new User(dataDto);
		this.saveUser(newUser);
		return newUser;
	}
	
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
}