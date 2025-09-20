package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
	private RestTemplate restTemplate;
	
	@Autowired
	private NotificationService notificationService;
	

	public Transaction createTransaction(TransactionDTO transaction) throws Exception {

		User sender = this.userService.findUserById(transaction.senderId());
		User receiver = this.userService.findUserById(transaction.receiverId());
		
		userService.validateTransaction(sender, transaction.value());
		
		
		// Autoriza a transação via serviço externo
		boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
		
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
	

	
	public boolean authorizeTransaction(User sender, BigDecimal value) {
		 try {
			    // Faz a requisição GET para o serviço externo de autorização
		        ResponseEntity<Map> authorizationResponse = 
		            restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
		        
		        if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
		        	// Extrai o corpo da resposta como um Map
		            Map<String, Object> responseBody = authorizationResponse.getBody();
		            
		            // Verifica se a resposta tem a estrutura esperada
		            if (responseBody != null && "success".equals(responseBody.get("status"))) {
		            	// Extrai o campo "authorization" do corpo da resposta
		                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
		                if (data != null && data.containsKey("authorization")) {
		                    return Boolean.TRUE.equals(data.get("authorization"));
		                }
		            }
		        }
		        return false;
		    } catch (Exception e) {
		        // Logar a exceção para debugging
		        System.err.println("Erro na autorização: " + e.getMessage());
		        return false;
		    }
		}
	


}
