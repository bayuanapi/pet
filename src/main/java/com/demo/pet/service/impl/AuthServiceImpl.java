package com.demo.pet.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.demo.pet.model.helper.JwtTokenUtil;
import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.reponse.JwtResponse;
import com.demo.pet.model.request.JwtRequest;
import com.demo.pet.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService  {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public BaseResponse auth(JwtRequest request) throws Exception {
		authenticate(request.getUserName(), request.getPassword());

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(request.getUserName());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return BaseResponse.createSuccessResponse(new JwtResponse(token));
	}
	
	private void authenticate(String userName, String password) throws Exception {
		Objects.requireNonNull(userName);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

}
