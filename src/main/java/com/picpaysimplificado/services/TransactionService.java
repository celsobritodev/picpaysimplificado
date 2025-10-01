package com.picpaysimplificado.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;


@Service
public class TransactionService {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private NotificationService notificationService;
	

	public Transaction createTransaction(TransactionDTO transaction) throws Exception {

		User sender = this.userService.findUserById(transaction.senderId());
		User receiver = this.userService.findUserById(transaction.receiverId());
		
		userService.validateTransaction(sender, transaction.value());
		
		
		// Autoriza a transação via serviço externo
		boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transaction.value());
		if(!isAuthorized) {
			throw new Exception("Transação não autorizada"); }
		
		Transaction newTransaction = new Transaction();
		newTransaction.setAmount(transaction.value());
		newTransaction.setSender(sender);
		newTransaction.setReceiver(receiver);
		newTransaction.setTimestamp(LocalDateTime.now());
		
		sender.setBalance(sender.getBalance().subtract(transaction.value()));
		receiver.setBalance(receiver.getBalance().add(transaction.value()));
		
		transactionRepository.save(newTransaction);
		userService.saveUser(sender);
		userService.saveUser(receiver);
		
		this.notificationService.sendNotification(sender, "Transação realizada com sucesso!");
		
		this.notificationService.sendNotification(receiver, "Transação recebida com sucesso!");
		
		return newTransaction;
		

	}

	// Exemplo de resposta do serviço externo de autorização
//	{
//		  "status": "success",
//		  "data": {
//		    "authorization": true
//		  }
//		}
//	
//	
	

	
	


}
