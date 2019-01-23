package com.ef.shorturl.dao;

import org.springframework.data.repository.CrudRepository;

import com.ef.shorturl.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);
}
