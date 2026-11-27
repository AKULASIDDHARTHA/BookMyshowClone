
package com.alpha.theaterservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.theaterservice.dto.CreateMovieDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.service.AdminService;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminser;

 
    @PostMapping("/admin/createMovie")
    public ResponseEntity<ResponseStructure<Movie>> createMovie(
            @RequestBody CreateMovieDto t) {

        return adminser.createMovie(t);
    }

   
    @DeleteMapping("/admin/movie/delete")
    public ResponseEntity<ResponseStructure<String>> deleteMovie(
            @RequestParam long id) {

        return adminser.deleteMovie(id);
    }

   
    @GetMapping("/admin/movie/find")
    public ResponseEntity<ResponseStructure<Movie>> findMovie(
            @RequestParam long id) {

        return adminser.findMovie(id);
    }
    
    @GetMapping("/admin/movie/all")
    public ResponseEntity<ResponseStructure<List<Movie>>> findAllMovies() {

        return adminser.findAllMovies();
    }
    
    
    @PutMapping("/admin/movie/update")
    public ResponseEntity<ResponseStructure<Movie>> updateMovie(
            @RequestBody CreateMovieDto dto) {

        ResponseStructure<Movie> response = adminser.updateMovie(dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/admin/movie/search")
    public ResponseEntity<ResponseStructure<List<Movie>>> findMovieByName(
            @RequestParam String title) {

        ResponseStructure<List<Movie>> response =
                adminser.findMovieByName(title);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

}

