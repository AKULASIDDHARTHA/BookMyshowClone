package com.alpha.customerservice.dto;

public class MovieDto {
	private String title;
	private String description;
	private String moviecast;
	private String languages;
	private Double duration;
	public MovieDto(String title, String description, String moviecast, String languages, Double duration) {
		super();
		this.title = title;
		this.description = description;
		this.moviecast = moviecast;
		this.languages = languages;
		this.duration = duration;
	}
	
	public MovieDto() {
		super();
	}

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
	public String getMoviecast() {
		return moviecast;
	}
	public void setMoviecast(String moviecast) {
		this.moviecast = moviecast;
	}
	public String getLanguages() {
		return languages;
	}
	public void setLanguages(String languages) {
		this.languages = languages;
	}
	public Double getDuration() {
		return duration;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	
}
