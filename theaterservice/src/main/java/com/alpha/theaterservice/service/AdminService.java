
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
import com.alpha.theaterservice.repository.MovieRepository;

@Service
public class AdminService {

    @Autowired
    private MovieRepository movieRepo;

    public ResponseEntity<ResponseStructure<Movie>> createMovie(CreateMovieDto t) {

        Movie movie = new Movie();

        movie.setTitle(t.getTitle());
        movie.setDescription(t.getDescription());
        movie.setCast(t.getMoviecast());
        movie.setDuration(t.getDuration());
        movie.setLanguages(t.getLanguage());

        Movie savedMovie = movieRepo.save(movie);

        ResponseStructure<Movie> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage("MOVIE CREATED SUCCESSFULLY");
        response.setData(savedMovie);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<String>> deleteMovie(long id) {

        ResponseStructure<String> response = new ResponseStructure<>();

        Optional<Movie> movieOptional = movieRepo.findById(id);

        if (movieOptional.isPresent()) {

            movieRepo.delete(movieOptional.get());

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("MOVIE DELETED SUCCESSFULLY");
            response.setData(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage("MOVIE NOT FOUND");
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

   
    public ResponseEntity<ResponseStructure<Movie>> findMovie(long id) {

        Optional<Movie> movieOptional = movieRepo.findById(id);

        ResponseStructure<Movie> response = new ResponseStructure<>();

        if (movieOptional.isPresent()) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("MOVIE FOUND");
            response.setData(movieOptional.get());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage("MOVIE NOT FOUND");
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
