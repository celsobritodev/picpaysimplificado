package com.picpaysimplificado.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpaysimplificado.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByDocument(String document);
	
	Optional<User> findById(Long id);

}
