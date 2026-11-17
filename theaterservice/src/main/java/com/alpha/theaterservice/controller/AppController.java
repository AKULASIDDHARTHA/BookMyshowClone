package com.alpha.theaterservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.theaterservice.dto.CreateTheaterDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.entity.Theater;
import com.alpha.theaterservice.service.AdminService;
import com.alpha.theaterservice.service.TheaterService;

@RestController
public class AppController {

    @Autowired
    private TheaterService tser;
    
    @Autowired
    private AdminService adminser;
    
    @PostMapping("/theater/createAccount")
    public ResponseEntity<ResponseStructure<Theater>> create(@RequestBody CreateTheaterDto t) {

        System.out.println("Theater Registered");
        
        return tser.createTheaterAccount(t);
    }
   
    @GetMapping("/theater/findById")
    public ResponseEntity<ResponseStructure<Theater>> findById(
            @RequestParam long id) {

        return tser.findTheater(id);
    }
    
    @DeleteMapping("/theater/deleteAccount")
    public ResponseEntity<ResponseStructure<Theater>> deleteAccount(@RequestParam long id) {
    	return tser.deleteAccount(id);
    }
    
    @GetMapping("/theater/movie/all")
    public ResponseEntity<ResponseStructure<List<Movie>>> findAllMovies() {

        return adminser.findAllMovies();
    }
    
    @PostMapping("/theater/createNewShow")
    public void createNewShow() {
    	
    }
}
	