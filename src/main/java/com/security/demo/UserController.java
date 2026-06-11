package com.security.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController 
{
	@Autowired
	private UserService service;
	
	@PostMapping("/register")
	public User register(@RequestBody User user)
	{
		return service.register(user);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody User user)
	{
		return service.login(user);
	}
	
	@GetMapping("/getAllUsers")
	public List<User> getAll()
	{
		return service.getAll();
	}
}
