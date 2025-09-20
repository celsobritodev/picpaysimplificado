package com.picpaysimplificado.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.NotificationDTO;

// Exemplo de resposta de falha do serviço externo
/*

{
	  "status": "fail",
	  "data": {
	    "message": "Route 'GET:/api/v1/notify' not found"
	  }
	}


*/

@Service
public class NotificationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);
        
        try {
            ResponseEntity<Map> notificationResponse =
                restTemplate.postForEntity("https://util.devi.tools/api/v1/notify",
                notificationRequest, Map.class);
            
            if (notificationResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = notificationResponse.getBody();
                
                // Verifica se a resposta indica sucesso
                if (responseBody != null && "success".equals(responseBody.get("status"))) {
                    System.out.println("Notificação enviada para o usuário: " + email);
                } else {
                    // Trata o caso de status "fail"
                    handleFailedNotification(responseBody, email);
                }
            } else {
                System.out.println("Erro HTTP ao enviar notificação: " + notificationResponse.getStatusCode());
                throw new RuntimeException("Serviço de notificação retornou erro: " + notificationResponse.getStatusCode());
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao enviar notificação para: " + email);
            throw new RuntimeException("Serviço de notificação indisponível", e);
        }
    }
    
    private void handleFailedNotification(Map<String, Object> responseBody, String email) {
        if (responseBody != null && "fail".equals(responseBody.get("status"))) {
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data != null && data.containsKey("message")) {
                String errorMessage = (String) data.get("message");
                System.out.println("Erro ao enviar notificação para " + email + ": " + errorMessage);
                throw new RuntimeException("Falha no serviço de notificação: " + errorMessage);
            }
        }
        throw new RuntimeException("Falha desconhecida no serviço de notificação");
    }
}