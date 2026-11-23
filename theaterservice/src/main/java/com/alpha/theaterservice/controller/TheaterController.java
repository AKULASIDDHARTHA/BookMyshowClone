package com.alpha.theaterservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.theaterservice.dto.CreateShowDto;
import com.alpha.theaterservice.dto.CreateTheaterDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.dto.UpdateShowDto;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.entity.Seat;
import com.alpha.theaterservice.entity.Show;
import com.alpha.theaterservice.entity.Theater;
import com.alpha.theaterservice.repository.MovieRepository;
import com.alpha.theaterservice.repository.SeatRepository;
import com.alpha.theaterservice.service.AdminService;
import com.alpha.theaterservice.service.TheaterService;

@RestController
public class TheaterController {

    @Autowired
    private TheaterService tser;
    
    @Autowired
    private SeatRepository seatRepo;
  
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
    

    @Autowired
    private AdminService adminser;
    @GetMapping("/theater/movie/all")
    public ResponseEntity<ResponseStructure<List<Movie>>> findAllMovies() {

        return adminser.findAllMovies();
    }
    @PostMapping("/theater/createNewShow")
    public ResponseEntity<ResponseStructure<String>> createNewShow(
            @RequestParam long theaterId,
            @RequestBody CreateShowDto dto) {

        return tser.CreateNewShow(dto, theaterId);
    }
    
    @Autowired
    private MovieRepository movieRepo;
    
    @GetMapping("/theater/findAvailableMovies")
    public ResponseEntity<ResponseStructure<List<Movie>>> findAvailableMovies() {

        return tser.findAvailableMovies();
    }
    
    @GetMapping("/theater/findAllPlayingShows")
    public ResponseEntity<ResponseStructure<List<Show>>> findallplayingShows(
            @RequestParam Long id) {
        return tser.findAllPlayingShows(id);
    }
    
    @GetMapping("/theater/findshow")
    public ResponseEntity<ResponseStructure<Show>> findshows(@RequestParam Long id){
    	return tser.findShow(id);
    }
    
    @PutMapping("/theater/updateShow")
    public ResponseEntity<ResponseStructure<Show>> updateShow(@RequestParam  Long id,@RequestBody UpdateShowDto dto) {
    	return tser.updateShow(id,dto);
    }
    
    @GetMapping("/findmovie")
    public ResponseEntity<ResponseStructure<List<Movie>>> findMovieByTitle(@RequestParam String moviename){
    	return tser.findMovieByTitle(moviename);
    }
    
    @GetMapping("/customer/buytickets")
    public ResponseEntity<ResponseStructure<List<Show>>> buyTickets(
            @RequestParam String moviename,
            @RequestParam String language,
            @RequestParam LocalDate date) {
        return tser.buyTickets(moviename, language, date);
    }
    
    @GetMapping("/shows/seats/available")
    public List<Seat> getAvailableSeats(@RequestParam long showId) {
        return tser.findAvailableSeats(showId);
    }
    
    @PostMapping("/shows/seats/lock")
    public ResponseEntity<List<Seat>> lockSeats(
            @RequestParam long showId,
            @RequestBody List<String> seatNumbers) {

        List<Seat> seats = tser.lockSeats(showId, seatNumbers);

        return ResponseEntity.ok(seats);
    }
 	
 }
	