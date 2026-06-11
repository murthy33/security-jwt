package com.security.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService 
{
	@Autowired
	UserRepository repo;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public User register(User user) 
	{
		User savedUser = new User();
		savedUser.setUsername(user.getUsername());
		savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		savedUser.setRole(user.getRole());
		repo.save(savedUser);
		return savedUser;
	}

	public String login(User user) {

	    User db_user = repo.findByUsername(user.getUsername())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!passwordEncoder.matches(user.getPassword(), db_user.getPassword())) 
	    {
	        throw new RuntimeException("Invalid credentials please check");
	    }

	    return jwtService.generateToken(db_user);
	}

	public List<User> getAll() 
	{
		System.out.println("Inside getAll");
		return repo.findAll();
	}
	
}
