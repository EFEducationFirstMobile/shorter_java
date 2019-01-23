package com.ef.shorturl.dao;

import org.springframework.stereotype.Component;

@Component
public class ShortUrlAutoGenerator {
	private int counter = 0;
	
	public synchronized String getNextShortUrl() {
		return String.valueOf(++counter);
	}
	
	public synchronized void reset() {
		counter = 0;
	}
}
