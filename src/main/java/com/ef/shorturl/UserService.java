package com.ef.shorturl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ef.shorturl.dao.UserRepository;
import com.ef.shorturl.model.User;

@Component
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}
}
