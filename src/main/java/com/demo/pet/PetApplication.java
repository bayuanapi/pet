package com.demo.pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.demo.pet.model.helper.LoggingClientHttpRequestInterceptor;

import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication
@EnableSwagger2WebMvc
@Import(SpringDataRestConfiguration.class)
public class PetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate restTemplate = restTemplateBuilder.build();
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		simpleClientHttpRequestFactory.setConnectTimeout(30000);
		simpleClientHttpRequestFactory.setReadTimeout(60000);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new LoggingClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		// set additional supported media types
		converter.setSupportedMediaTypes(
				Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(converter);
		return restTemplate;
	}

}
