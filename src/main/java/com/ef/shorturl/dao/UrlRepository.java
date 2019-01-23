package com.ef.shorturl.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ef.shorturl.model.Url;

public interface UrlRepository extends CrudRepository<Url, Long> {

	Url findByShortUrl(String shortUrl);

	List<Url> findByCreatedByUsernameOrderByCreated(String name);
}
