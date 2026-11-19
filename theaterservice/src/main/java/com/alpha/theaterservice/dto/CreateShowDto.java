package com.alpha.theaterservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class CreateShowDto {
	private String title;
	private String description;
	private LocalDate date;
	private LocalTime time;
	private String language;
	private double duration;
	private int screenno;
	private Long movieId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public int getScreenno() {
		return screenno;
	}
	public void setScreenno(int screenno) {
		this.screenno = screenno;
	}
	public long getMovieId() {
	    return movieId;
	}

	public void setMovieId(long movieId) {
	    this.movieId = movieId;
	}
	
}
