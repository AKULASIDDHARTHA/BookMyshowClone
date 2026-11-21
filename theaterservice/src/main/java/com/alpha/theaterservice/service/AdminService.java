
package com.alpha.theaterservice.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alpha.theaterservice.dto.CreateMovieDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.exception.MovieNotFoundException;
import com.alpha.theaterservice.repository.MovieRepository;

@Service
public class AdminService {

    @Autowired
    private MovieRepository movieRepo;
    
    public ResponseEntity<ResponseStructure<Movie>> createMovie(CreateMovieDto dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setMoviecast(dto.getMoviecast());
        movie.setDuration(dto.getDuration());
        movie.setLanguages(dto.getLanguage());

        Movie savedMovie = movieRepo.save(movie);

        ResponseStructure<Movie> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage("MOVIE CREATED SUCCESSFULLY");
        response.setData(savedMovie);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<String>> deleteMovie(long id) {

        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("MOVIE NOT FOUND"));

        movieRepo.delete(movie);

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("MOVIE DELETED SUCCESSFULLY");
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   
    public ResponseEntity<ResponseStructure<Movie>> findMovie(long id) {

        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("MOVIE NOT FOUND"));

        ResponseStructure<Movie> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("MOVIE FOUND");
        response.setData(movie);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // GET ALL MOVIES
    public ResponseEntity<ResponseStructure<List<Movie>>> findAllMovies() {
        List<Movie> movies = movieRepo.findAll();
        ResponseStructure<List<Movie>> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("ALL MOVIES FETCHED SUCCESSFULLY");
        response.setData(movies);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
