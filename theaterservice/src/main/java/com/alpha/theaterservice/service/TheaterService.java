package com.alpha.theaterservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alpha.theaterservice.dto.CreateShowDto;
import com.alpha.theaterservice.dto.CreateTheaterDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.dto.UpdateShowDto;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.entity.Show;
import com.alpha.theaterservice.entity.Theater;
import com.alpha.theaterservice.exception.InvalidScreenNumberException;
import com.alpha.theaterservice.exception.MovieNotFoundException;
import com.alpha.theaterservice.exception.ShowAlreadyExistsException;
import com.alpha.theaterservice.exception.ShowNotFoundException;
import com.alpha.theaterservice.exception.TheaterAlreadyExistWithContactException;
import com.alpha.theaterservice.exception.TheaterNotFoundException;
import com.alpha.theaterservice.repository.AdminRepository;
import com.alpha.theaterservice.repository.MovieRepository;
import com.alpha.theaterservice.repository.ShowRepository;
import com.alpha.theaterservice.repository.TheaterRepository;

@Service
public class TheaterService {

	@Autowired
	private TheaterRepository theaterRepo;

	@Autowired
	private MovieRepository movieRepo;

	@Autowired
	private ShowRepository showRepo;

//	private AdminRepository adminRepo;

	public ResponseEntity<ResponseStructure<Theater>> createTheaterAccount(CreateTheaterDto createTheaterDto) {
		Optional<Theater> theaterOptional = theaterRepo.findByContactnumber(createTheaterDto.getContactnumber());
		System.out.println("Received Contact: " + createTheaterDto.getContactnumber());
		System.out.println("Is Contact Present: " + theaterOptional.isPresent());
		if (theaterOptional.isPresent()) {
			throw new TheaterAlreadyExistWithContactException();
		}
		Theater newthTheater = new Theater();
		newthTheater.setName(createTheaterDto.getName());
		newthTheater.setAddress(createTheaterDto.getAddress());
		newthTheater.setCity(createTheaterDto.getCity());
		newthTheater.setContactnumber(createTheaterDto.getContactnumber());
		newthTheater.setDescription(createTheaterDto.getDescription());
		newthTheater.setTotalscreens(createTheaterDto.getTotalscreens());
		newthTheater.setStatus("CLOSED");
		newthTheater.setMailid(createTheaterDto.getMailid());
		Theater savedthTheater = theaterRepo.save(newthTheater);
		ResponseStructure<Theater> responseStructure = new ResponseStructure<Theater>();
		responseStructure.setStatuscode(HttpStatus.CREATED.value());
		responseStructure.setMessage("NEW THEATER CREATED SUCCESSFULLY");
		responseStructure.setData(savedthTheater);
		return new ResponseEntity<ResponseStructure<Theater>>(responseStructure, HttpStatus.CREATED);
	}

	// FindTheater
	public ResponseEntity<ResponseStructure<Theater>> findTheater(long id) {
		Theater theater = theaterRepo.findById(id).orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));
		ResponseStructure<Theater> responseStructure = new ResponseStructure<>();
		responseStructure.setStatuscode(HttpStatus.OK.value());
		responseStructure.setMessage("THEATER FOUND SUCCESSFULLY");
		responseStructure.setData(theater);

		return new ResponseEntity<>(responseStructure, HttpStatus.OK);
	}

	// Delete
	public ResponseEntity<ResponseStructure<Theater>> deleteAccount(long id) {

		Theater theater = theaterRepo.findById(id).orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));
		theaterRepo.delete(theater);
		ResponseStructure<Theater> responseStructure = new ResponseStructure<>();
		responseStructure.setStatuscode(HttpStatus.OK.value());
		responseStructure.setMessage("THEATER DELETED SUCCESSFULLY");
		responseStructure.setData(null);

		return new ResponseEntity<>(responseStructure, HttpStatus.OK);
	}

	// Find Movies
	public ResponseEntity<ResponseStructure<List<Movie>>> findAvailableMovies() {
		List<Movie> movies = movieRepo.findAll();
		ResponseStructure<List<Movie>> response = new ResponseStructure<>();
		response.setStatuscode(HttpStatus.OK.value());
		response.setMessage("AVAILABLE MOVIES");
		response.setData(movies);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//creating new show
public ResponseEntity<ResponseStructure<String>> CreateNewShow(CreateShowDto dto, long theaterId) {

		// 1. Find theater by ID
		Theater th = theaterRepo.findById(theaterId)
				.orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));

		// 2. Check screen number
		if (dto.getScreenno() < 1 || dto.getScreenno() > th.getTotalscreens()) {
			throw new InvalidScreenNumberException("INVALID SCREEN NUMBER");
		}

		// 3. Check whether show already exists
		Optional<Show> showOptional = showRepo.findByDateAndTimeAndScreennoAndTheater_Id(dto.getDate(), dto.getTime(),
				dto.getScreenno(), theaterId);

		if (showOptional.isPresent()) {
			throw new ShowAlreadyExistsException("SHOW ALREADY EXISTS");
		}

		// 4. Find movie by ID
		Movie m = movieRepo.findById(dto.getMovieId()).orElseThrow(() -> new MovieNotFoundException("MOVIE NOT FOUND"));

		// 5. Create new Show
		Show sh = new Show();

		sh.setTitle(m.getTitle());
		sh.setDescription(dto.getDescription());
		sh.setDate(dto.getDate());
		sh.setTime(dto.getTime());
		sh.setLanguage(dto.getLanguage());
		sh.setDuration(dto.getDuration());
		sh.setScreenno(dto.getScreenno());

		// Set movie and theater
		sh.setMovie(m);
		sh.setTheater(th);

		// Save show
		Show savedShow = showRepo.save(sh);

		// Add show to theater
		if (th.getAllplayingshows() == null) {
			th.setAllplayingshows(new ArrayList<>());
		}

		th.getAllplayingshows().add(savedShow);

		theaterRepo.save(th);

		// Success response
		ResponseStructure<String> response = new ResponseStructure<>();

		response.setStatuscode(HttpStatus.CREATED.value());
		response.setMessage("NEW SHOW CREATED SUCCESSFULLY");
		response.setData("Show created successfully");

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

//find all avaiable movie
	public ResponseEntity<ResponseStructure<List<Movie>>> findAllAvailableMovies() {
		List<Movie> movies = movieRepo.findAll();
		ResponseStructure<List<Movie>> response = new ResponseStructure<>();
		response.setStatuscode(HttpStatus.OK.value());
		response.setMessage("ALL AVAILABLE MOVIES");
		response.setData(movies);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<List<Show>>> findAllPlayingShows(Long id) {

	    Theater theater = theaterRepo.findById(id)
	            .orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));

	    List<Show> shows = theater.getAllplayingshows();
	    ResponseStructure<List<Show>> response = new ResponseStructure<>();
	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("ALL PLAYING SHOWS");
	    response.setData(shows);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//FindShow
	public ResponseEntity<ResponseStructure<Show>> findShow(Long id) {

	    Show show = showRepo.findById(id)
	            .orElseThrow(() -> new ShowNotFoundException("SHOW NOT FOUND"));

	    ResponseStructure<Show> response = new ResponseStructure<>();

	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("SHOW FOUND");
	    response.setData(show);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<Show>> updateShow(Long id, UpdateShowDto dto) {
	    Show show = showRepo.findById(id)
	            .orElseThrow(() -> new ShowNotFoundException("SHOW NOT FOUND"));
	    show.setDate(dto.getDate());
	    show.setTime(dto.getTime());
	    show.setScreenno(dto.getScreenno());
	    Show updatedShow = showRepo.save(show);
	    ResponseStructure<Show> response = new ResponseStructure<>();
	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("SHOW UPDATED SUCCESSFULLY");
	    response.setData(updatedShow);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<Movie>> findMovieByTitle(String moviename) {
		Movie movie=movieRepo.findByTitleContainsIgnoreCase(moviename)
				.orElseThrow(()->new MovieNotFoundException("Movie Not Found"));
	    ResponseStructure<Movie> response = new ResponseStructure<>();
	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("Movie found");
	    response.setData(movie);
	    return new ResponseEntity<ResponseStructure<Movie>>(response,HttpStatus.OK);
	}	
	
	public ResponseEntity<ResponseStructure<List<Show>>> buyTickets(
	        String moviename, String language, LocalDate date) {

	    List<Show> shows = showRepo
	            .findByTitleContainingIgnoreCaseAndLanguageIgnoreCaseAndDate(
	                    moviename, language, date);

	    ResponseStructure<List<Show>> response = new ResponseStructure<>();
	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("AVAILABLE SHOWS");
	    response.setData(shows);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
