package com.demo.pet.service;

import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.request.JwtRequest;

public interface AuthService {

	BaseResponse auth(JwtRequest request) throws Exception;

}
