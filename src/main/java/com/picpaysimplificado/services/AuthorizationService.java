package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;

@Service
public class AuthorizationService {
	
	@Autowired
	private RestTemplate restTemplate;
	

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
