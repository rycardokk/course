package com.ead.course.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration //classe de configuracao
public class RestTemplateConfig { //restTemplate serve para fazer requisições externas

    @Bean //inicia um bean dentro de uma classe de config
    //metodo produtor com o bean para retornar o RT para conseguir criar os pontos de injecao onde for necessário, como em UserClient
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        //Do any additional configuration here (customizações, exceções globais)
        return builder.build();
    }


}
