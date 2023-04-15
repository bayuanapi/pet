package com.demo.pet.model.reponse;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	private Integer status;
	private String message;
	private T data;

	public static BaseResponse createSuccessResponse() {
		BaseResponse response = new BaseResponse();
		response.setStatus(0);
		response.setMessage("ok");
		return response;
	}

	public static BaseResponse createSuccessResponse(Object data) {
		BaseResponse response = createSuccessResponse();
		response.setData(data);
		return response;
	}

	public static BaseResponse createFailedResponse(Integer statusCode, String message) {
		BaseResponse response = new BaseResponse();
		response.setStatus(statusCode);
		response.setMessage(message);
		return response;
	}
}
