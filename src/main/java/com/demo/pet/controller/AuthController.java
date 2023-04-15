package com.demo.pet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.request.JwtRequest;
import com.demo.pet.service.AuthService;

@RestController
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping(value = "/auth")
	public BaseResponse createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
		return authService.auth(request);
	}

}
