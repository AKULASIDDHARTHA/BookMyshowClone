package com.alpha.theaterservice.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Show {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne
	private Movie movie;
	
	@ManyToOne
	private Theater theater;
	@OneToMany(cascade = CascadeType.ALL)
	private List<Seat> seats;
	private String title;
	private String description;
	private double duration;
	private String language;
	private int screenno;
	private LocalTime time;
	private LocalDate date;
	private Double price;
	
	public Show() {
		super();
	}
	public Show(Movie movie, String title, String description, double duration, String language, int screenno,LocalTime time,LocalDate date,Theater theater,Double price,List<Seat> seats) {
		this.movie = movie;
		this.title = title;
		this.description = description;
		this.duration = duration;
		this.language = language;
		this.screenno = screenno;
		this.time=time;
		this.date=date;
		this.theater=theater;
		this.price=price;
		this.seats=seats;	
	}
	public List<Seat> getSeats() {
	    return seats;
	}

	public void setSeats(List<Seat> seats) {
	    this.seats = seats;
	}
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate Date) {
		this.date = Date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
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
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getScreenno() {
		return screenno;
	}
	public void setScreenno(int screenno) {
		this.screenno = screenno;
	}
	public Theater getTheater() {
		return theater;
	}
	public void setTheater(Theater theater) {
		this.theater = theater;
	}

}
