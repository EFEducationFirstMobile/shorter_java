package com.ef.shorturl.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ef.shorturl.controller.dto.UrlDTO;
import com.ef.shorturl.controller.dto.UrlFullDTO;
import com.ef.shorturl.dao.ShortUrlAutoGenerator;
import com.ef.shorturl.dao.UrlRepository;
import com.ef.shorturl.dao.UserRepository;
import com.ef.shorturl.model.Url;

@RestController
public class UrlController {

	@Value("${base_url}")
	private String baseUrl;

	@Autowired
	private UrlRepository urlRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ShortUrlAutoGenerator shortUrlAutoGenerator;
	
	@GetMapping("/")
	public List<UrlFullDTO> allUrls(Principal principal) {
		System.out.println(principal.getName());
		return urlRepository.findByCreatedByUsernameOrderByCreated(principal.getName()).stream().map(url -> urlToFullDTO(url))
				.collect(Collectors.toList());
	}

	@PostMapping("/")
	public UrlDTO shortenUrl(@Valid @RequestBody UrlDTO urlDto, Errors errors, Principal principal) {
		if (errors.hasErrors()) {
			throw new UrlRequestInvalidException();
		} else if (StringUtils.isEmpty(urlDto.shorturl)) {
			urlDto.shorturl = shortUrlAutoGenerator.getNextShortUrl();
		} else {
			if (urlRepository.findByShortUrl(urlDto.shorturl) != null) {
				throw new UrlAlreadyExistsException();
			}
		}

		Url url = new Url();
		url.setUrl(urlDto.url);
		url.setShortUrl(urlDto.shorturl);
		url.setCreatedBy(userRepository.findByUsername(principal.getName()));
		url.setCreated(new Date());

		urlRepository.save(url);

		return urlToDTO(url);
	}

	@GetMapping("/{shortUrl}")
	public UrlFullDTO status(@PathVariable String shortUrl, Principal principal) throws IOException {
		Url url = urlRepository.findByShortUrl(shortUrl);
		if (url == null) {
			throw new UrlNotFoundException();
		}

		return urlToFullDTO(url);
	}
	
	@GetMapping("/{shortUrl}/redirect")
	public void redirect(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
		Url url = urlRepository.findByShortUrl(shortUrl);
		if (url == null) {
			throw new UrlNotFoundException();
		}

		incrementAccessed(url);

		response.sendRedirect(url.getUrl());
	}

	private void incrementAccessed(Url url) {
		url.setAccessed(url.getAccessed() + 1);
		urlRepository.save(url);
	}

	private String getAbsoluteUrl(String shortUrl) {
		return baseUrl + "/" + shortUrl;
	}

	private UrlDTO urlToDTO(Url url) {
		UrlDTO urlDTO = new UrlDTO();
		
		urlDTO.shorturl = getAbsoluteUrl(url.getShortUrl());
		urlDTO.url = url.getUrl();
		
		return urlDTO;
	}

	private UrlFullDTO urlToFullDTO(Url url) {
		UrlFullDTO urlFullDTO = new UrlFullDTO();

		urlFullDTO.accessed = url.getAccessed();
		urlFullDTO.created = url.getCreated();
		urlFullDTO.shorturl = getAbsoluteUrl(url.getShortUrl());
		urlFullDTO.url = url.getUrl();
		
		return urlFullDTO;
	}

}

class UrlNotFoundException extends ResponseStatusException {
	private static final long serialVersionUID = 4435179056868697418L;

	UrlNotFoundException() {
		super(HttpStatus.NOT_FOUND, "URL not found");
	}
}

class UrlAlreadyExistsException extends ResponseStatusException {
	private static final long serialVersionUID = 8551159894381334861L;

	UrlAlreadyExistsException() {
		super(HttpStatus.BAD_REQUEST, "Could not create new link. One with the given `shorturl` already exists");
	}
}

class UrlRequestInvalidException extends ResponseStatusException {
	private static final long serialVersionUID = 8551159894381334861L;

	UrlRequestInvalidException() {
		super(HttpStatus.BAD_REQUEST, "Make sure the `shorturl` field is no more than 23 alphanumeric chars.");
	}
}

