package com.alpha.theaterservice.exception;

public class TheaterAlreadyExistWithContactException extends RuntimeException {
	public TheaterAlreadyExistWithContactException() {
		super("Theater already exists with this contact number");
	}
}
