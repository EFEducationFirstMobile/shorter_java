package com.ef.shorturl.controller.dto;

import javax.validation.constraints.Pattern;

public class UrlDTO {
	public String url;
	
	@Pattern(regexp = "^[0-9[a-z]]{0,23}$")
	public String shorturl;
}