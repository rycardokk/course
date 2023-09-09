package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    // URL base do serviço que será chamado.
    @Value("${ead.api.url.authuser}")
    String REQUEST_URI_AUTHUSER; // O service Registry vai deixar dinâmico posteriormente

    // Método que faz uma chamada HTTP para buscar cursos de um usuário.
    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        List<UserDto> searchResult = null;
        ResponseEntity<ResponsePageDto<UserDto>> result = null;

        // Constrói a URL para a chamada HTTP com base nos parâmetros fornecidos.
        String url = REQUEST_URI_AUTHUSER + utilsService.CreateUrlGetAllUsersByCourse(courseId, pageable);

        // Logging para registrar a URL da solicitação.
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try {
            // Define o tipo de resposta esperada.
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};

            // Realiza a chamada HTTP GET e armazena a resposta.
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

            // Obtém o conteúdo da resposta.
            searchResult = Objects.requireNonNull(result.getBody()).getContent();

            // Logging para registrar o número de elementos na resposta.
            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            // Em caso de erro na chamada HTTP, registra o erro.
            log.error("Error request /courses {} ", e);
        }

        // Logging para indicar o término da solicitação.
        log.info("Ending request /users courseId {} ", courseId);

        // Retorna a resposta obtida da chamada HTTP.
        assert result != null;
        return result.getBody();
    }


    public ResponseEntity<UserDto> getOneUserById(UUID userId){
        String url = REQUEST_URI_AUTHUSER + "/users/" + userId;
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }


    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URI_AUTHUSER + "/users/" + userId + "/courses/subscription";
        var courseUserDto = new CourseUserDto();
        courseUserDto.setUserId(userId);
        courseUserDto.setCourseId(courseId);
        restTemplate.postForObject(url, courseUserDto, String.class); //envia o dto para o Microservice User
    }
}
