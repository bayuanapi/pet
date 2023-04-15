package com.demo.pet.model.helper;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

		Integer code = httpResponse.getStatusCode().value();
		return ((!code.equals(400) && !code.equals(422) && !code.equals(404))
				&& httpResponse.getStatusCode().series() == CLIENT_ERROR
				|| httpResponse.getStatusCode().series() == SERVER_ERROR);
	}
}
