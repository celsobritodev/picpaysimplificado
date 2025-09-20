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
	
	private String password;
	
	@Column(unique = true, nullable = false)
	private String document;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Enumerated(EnumType.STRING)
	private UserType userType;
	
	private BigDecimal balance;
	

	public User(UserDTO dataDTO) {
		this.firstName = dataDTO.firstName();
		this.lastName = dataDTO.lastName();
		this.password = dataDTO.password();
		this.document = dataDTO.document();
		this.email = dataDTO.email();
		this.userType = dataDTO.userType();
		this.balance = dataDTO.balance();


	
	}
	
	

}
