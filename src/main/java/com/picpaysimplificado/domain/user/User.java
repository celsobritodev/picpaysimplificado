package com.picpaysimplificado.domain.user;

import java.math.BigDecimal;

import com.picpaysimplificado.dtos.UserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	@Column(unique = true, nullable = false)
	private String document;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	private String password;
	
	private BigDecimal balance;
	
	@Enumerated(EnumType.STRING)
	private UserType userType;
	
	public User(UserDTO dataDTO) {
		this.firstName = dataDTO.firstName();
		this.lastName = dataDTO.lastName();
		this.balance = dataDTO.balance();
		this.userType = dataDTO.userType();
		this.password = dataDTO.password();
		this.email = dataDTO.email();
	}
	
	

}
