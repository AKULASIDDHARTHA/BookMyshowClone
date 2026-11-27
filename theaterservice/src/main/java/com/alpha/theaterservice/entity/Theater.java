package com.alpha.theaterservice.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Theater {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String description;
	private String city;
	private long contactnumber;
	private String mailid;
	private int totalscreens;
	private String status;	
	
	@OneToOne(cascade = CascadeType.ALL)
	private Address address;
	@JsonIgnore
	@OneToMany
	private List<Show> allplayingshows;
	
	public Theater(String name, String description, String city, long contactnumber, String mailid,
			int totalscreens, String status, Address address, List<Show> allplayingshows) {
		this.name = name;
		this.description = description;
		this.city = city;
		this.contactnumber = contactnumber;
		this.mailid = mailid;
		this.totalscreens = totalscreens;
		this.status = status;
		this.address = address;
		this.allplayingshows = allplayingshows;
	}
	public Theater() {
		super();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public long getContactnumber() {
		return contactnumber;
	}
	public void setContactnumber(long contactnumber) {
		this.contactnumber = contactnumber;
	}
	public String getMailid() {
		return mailid;
	}
	public void setMailid(String mailid) {
		this.mailid = mailid;
	}
	public int getTotalscreens() {
		return totalscreens;
	}
	public void setTotalscreens(int totalscreens) {
		this.totalscreens = totalscreens;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public List<Show> getAllplayingshows() {
		return allplayingshows;
	}
	public void setAllplayingshows(List<Show> allplayingshows) {
		this.allplayingshows = allplayingshows;
	}

}
