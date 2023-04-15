package com.demo.pet.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		if ("user".equals(userName)) {
			return new org.springframework.security.core.userdetails.User("user", passwordEncoder.encode("password"),
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + userName);
		}
	}
}