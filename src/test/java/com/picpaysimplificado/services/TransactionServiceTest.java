package com.picpaysimplificado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;

//import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;

public class TransactionServiceTest {
	
	@Mock
	private UserService userService;

	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private AuthorizationService authorizationService;
	
	@Mock
	private NotificationService notificationService;
	
	
	@Autowired
	@InjectMocks
	private TransactionService transactionService;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);
		
	}
	
	
	
	@Test
	@DisplayName("Should create transation successfully when everything is OK")
	void createTransactionCase1() throws Exception{
		User sender = new User(1L,"Maria","Souza","99999901","maria@gmail.com","123",UserType.COMMON,new BigDecimal(10));
		User receiver = new User(2L,"Joao","Clemous","99999902","joao@gmail.com","456",UserType.COMMON,new BigDecimal(10));
		
		when(userService.findUserById(1L)).thenReturn(sender);
		when(userService.findUserById(2L)).thenReturn(receiver);
		
		when(authorizationService.authorizeTransaction(any(),any())).thenReturn(true);
		
		TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10),1L,2L);
		transactionService.createTransaction(transactionDTO);
		
		// verificar se o transactionRepository foi chamado
		verify(transactionRepository,times(1)).save(any());
		
		sender.setBalance(new BigDecimal(0));
		verify(userService,times(1)).saveUser(sender);
		
		receiver.setBalance(new BigDecimal(20));
		verify(userService,times(1)).saveUser(receiver);
		
		verify(notificationService,times(1)).sendNotification(sender, "Enviada com sucesso");
		verify(notificationService,times(1)).sendNotification(receiver, "Recebida com sucesso");
		
		
	}
	
	
	@Test
	@DisplayName("Should throw Exception when transation is now allowed")
	void createTransactionCase2() throws Exception{
		User sender = new User(1L,"Maria","Souza","99999901","maria@gmail.com","123",UserType.COMMON,new BigDecimal(10));
		User receiver = new User(2L,"Joao","Clemous","99999902","joao@gmail.com","456",UserType.COMMON,new BigDecimal(10));
		
		when(userService.findUserById(1L)).thenReturn(sender);
		when(userService.findUserById(2L)).thenReturn(receiver);
		
		when(authorizationService.authorizeTransaction(any(),any())).thenReturn(false);
		
		Exception thrown = Assertions.assertThrows(Exception.class,() -> {
			TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10),1L,2L);
			transactionService.createTransaction(transactionDTO);
		});
		
		Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
		
	}

	
	

}
