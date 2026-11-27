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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alpha.theaterservice.dto.CreateShowDto;
import com.alpha.theaterservice.dto.CreateTheaterDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.dto.UpdateShowDto;
import com.alpha.theaterservice.entity.Movie;
import com.alpha.theaterservice.entity.Seat;
import com.alpha.theaterservice.entity.Show;
import com.alpha.theaterservice.entity.Theater;
import com.alpha.theaterservice.exception.InvalidScreenNumberException;
import com.alpha.theaterservice.exception.MovieNotFoundException;
import com.alpha.theaterservice.exception.SeatNotAvailableException;
import com.alpha.theaterservice.exception.SeatNotFoundException;
import com.alpha.theaterservice.exception.ShowAlreadyExistsException;
import com.alpha.theaterservice.exception.ShowNotFoundException;
import com.alpha.theaterservice.exception.TheaterAlreadyExistWithContactException;
import com.alpha.theaterservice.exception.TheaterNotFoundException;
import com.alpha.theaterservice.repository.AdminRepository;
import com.alpha.theaterservice.repository.MovieRepository;
import com.alpha.theaterservice.repository.SeatRepository;
import com.alpha.theaterservice.repository.ShowRepository;
import com.alpha.theaterservice.repository.TheaterRepository;

import jakarta.transaction.Transactional;

@Service
public class TheaterService {

	@Autowired
	private TheaterRepository theaterRepo;

	@Autowired
	private MovieRepository movieRepo;

	@Autowired
	private ShowRepository showRepo;
	
	@Autowired
	private SeatRepository seatRepo; 


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

public ResponseEntity<ResponseStructure<String>> CreateNewShow(
        CreateShowDto dto, long theaterId) {

    // 1. Find theater
    Theater th = theaterRepo.findById(theaterId)
            .orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));

    // 2. Check screen number
    if (dto.getScreenno() < 1 || dto.getScreenno() > th.getTotalscreens()) {
        throw new InvalidScreenNumberException("INVALID SCREEN NUMBER");
    }

    // 3. Check whether show already exists
    Optional<Show> showOptional =
            showRepo.findByDateAndTimeAndScreennoAndTheater_Id(
                    dto.getDate(), dto.getTime(), dto.getScreenno(), theaterId);

    if (showOptional.isPresent()) {
        throw new ShowAlreadyExistsException("SHOW ALREADY EXISTS");
    }

    // 4. Find movie
    Movie m = movieRepo.findById(dto.getMovieId())
            .orElseThrow(() -> new MovieNotFoundException("MOVIE NOT FOUND"));

    // 5. Create show
    Show sh = new Show();
    sh.setTitle(m.getTitle());
    sh.setDescription(dto.getDescription());
    sh.setDate(dto.getDate());
    sh.setTime(dto.getTime());
    sh.setLanguage(dto.getLanguage());
    sh.setDuration(dto.getDuration());
    sh.setScreenno(dto.getScreenno());
    sh.setPrice(dto.getPrice());
    sh.setMovie(m);
    sh.setTheater(th);

    // 6. Generate seats based on capacity
    List<Seat> seats = generateSeats(dto.getCapacity(), sh);
    sh.setSeats(seats);

    // 7. Save show and seats
    Show savedShow = showRepo.save(sh);

    // 8. Add show to theater
    if (th.getAllplayingshows() == null) {
        th.setAllplayingshows(new ArrayList<>());
    }

    th.getAllplayingshows().add(savedShow);
    theaterRepo.save(th);

    // 9. Success response
    ResponseStructure<String> response = new ResponseStructure<>();

    response.setStatuscode(HttpStatus.CREATED.value());
    response.setMessage("NEW SHOW CREATED SUCCESSFULLY");
    response.setData("Show created successfully");

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
public List<Seat> generateSeats(int capacity, Show show) {

    if (capacity <= 0) {
        throw new IllegalArgumentException("Capacity must be positive");
    }

    List<Seat> seats = new ArrayList<>();

    String[] categories = {"SILVER", "GOLD", "DIAMOND"};

    int baseCapacity = capacity / 3;
    int remainder = capacity % 3;

    char row = 'A';

    for (int i = 0; i < 3; i++) {

        int categoryCapacity = baseCapacity;

        if (i < remainder) {
            categoryCapacity++;
        }

        int seatNumber = 1;

        for (int j = 1; j <= categoryCapacity; j++) {
            Seat seat = new Seat();
            seat.setSeatno(row + String.valueOf(seatNumber));
            seat.setCategory(categories[i]);
            seat.setStatus("AVAILABLE");
            seat.setShow(show);

            seats.add(seat);

            seatNumber++;

            if (seatNumber > 10) {
                seatNumber = 1;
                row++;
            }
        }

        if (seatNumber != 1) {
            row++;
        }
    }

    return seats;
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

	public ResponseEntity<ResponseStructure<List<Movie>>> findMovieByTitle(String moviename) {

	    List<Movie> movies = movieRepo.findByTitleContainsIgnoreCase(moviename);

	    if (movies.isEmpty()) {
	        throw new MovieNotFoundException("MOVIES NOT FOUND");
	    }

	    ResponseStructure<List<Movie>> response = new ResponseStructure<>();
	    response.setStatuscode(HttpStatus.OK.value());
	    response.setMessage("Movies found");
	    response.setData(movies);

	    return new ResponseEntity<>(response, HttpStatus.OK);
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
	
	
	public List<Seat> findAvailableSeats(long showId) {
        if (!showRepo.existsById(showId)) {
            throw new ShowNotFoundException("SHOW NOT FOUND");
        }
        return seatRepo.findByShow_IdAndStatus(showId, "AVAILABLE");
    }

	
	@Transactional
	public List<Seat> lockSeats(long showId, List<String> seatNumbers) {

		for (int i = 0; i < seatNumbers.size(); i++) {
		    seatNumbers.set(i, seatNumbers.get(i).toUpperCase());
		}
	    List<Seat> seats =
	            seatRepo.findByShow_IdAndSeatnoIn(showId, seatNumbers);

	    if (seats.size() != seatNumbers.size()) {
	        throw new SeatNotFoundException("SELECTED SEATS NOT FOUND");
	    }
	    for (Seat seat : seats) {
	    	if (!seat.getStatus().equalsIgnoreCase("AVAILABLE")) {
	    	    throw new SeatNotAvailableException(
	    	            seat.getSeatno() + " is not available");
	    	}
	    }
	    for (Seat seat : seats) {
	        seat.setStatus("LOCKED");
	    }
	    seatRepo.saveAll(seats);
	    return seats;
	}
	
	
	public ResponseStructure<Theater> updateTheater(long theaterid, CreateTheaterDto dto) {

	    Theater theater = theaterRepo.findById(theaterid)
	            .orElseThrow(() ->
	                new TheaterNotFoundException("THEATER NOT FOUND"));

	    theater.setName(dto.getName());
	    theater.setDescription(dto.getDescription());
	    theater.setCity(dto.getCity());
	    theater.setContactnumber(dto.getContactnumber());
	    theater.setMailid(dto.getMailid());
	    theater.setTotalscreens(dto.getTotalscreens());
	    theater.setAddress(dto.getAddress());

	    theaterRepo.save(theater);

	    ResponseStructure<Theater> response = new ResponseStructure<>();

	    response.setStatuscode(200);
	    response.setMessage("THEATER DETAILS UPDATED SUCCESSFULLY");
	    response.setData(theater);

	    return response;
	}
	
	
	// Delete Show by showid and theater id
		public ResponseEntity<ResponseStructure<Show>> deleteShow(long theaterId, long showId) {
		    // Check theater
		    Theater theater = theaterRepo.findById(theaterId)
		            .orElseThrow(() ->
		                    new TheaterNotFoundException("Theater is not found"));
		    // Check show
		    Show show = showRepo.findById(showId)
		            .orElseThrow(() ->
		                    new ShowNotFoundException("Show is not found"));
		    // checking show belongs to this theater..
		    if (show.getTheater() == null ||
		            show.getTheater().getId() != theaterId) {
		        throw new ShowNotFoundException(
		                "Show does not belong to this theater");
		    }
		    // Remove show from theater's list
		    if (theater.getAllplayingshows() != null) {
		        theater.getAllplayingshows().removeIf(
		                s -> s.getId() == showId
		        );
		        theaterRepo.save(theater);
		    }
		    showRepo.delete(show);
		    ResponseStructure<Show> rs = new ResponseStructure<>();
		    rs.setStatuscode(HttpStatus.OK.value());
		    rs.setMessage("Show deleted successfully");
		    rs.setData(show);
		    return new ResponseEntity<>(rs, HttpStatus.OK);
		}
		
		public ResponseStructure<List<Show>> findShowsByDate(long theaterid, LocalDate date) {

		    Theater theater = theaterRepo.findById(theaterid)
		            .orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));

		    List<Show> shows = showRepo.findByTheater_IdAndDate(theaterid, date);

		    if (shows.isEmpty()) {
		        throw new ShowNotFoundException("NO SHOWS FOUND ON THIS DATE");
		    }

		    ResponseStructure<List<Show>> response = new ResponseStructure<>();
		    response.setStatuscode(200);
		    response.setMessage("SHOWS FOUND SUCCESSFULLY");
		    response.setData(shows);

		    return response;
		}
		
		public ResponseStructure<List<Show>> findShowsByMovie(long theaterid, String title) {

		    theaterRepo.findById(theaterid)
		            .orElseThrow(() -> new TheaterNotFoundException("THEATER NOT FOUND"));

		    List<Show> shows =
		            showRepo.findByTheater_IdAndTitleContainingIgnoreCase(theaterid, title);

		    if (shows.isEmpty()) {
		        throw new ShowNotFoundException("NO SHOWS FOUND FOR THIS MOVIE");
		    }

		    ResponseStructure<List<Show>> response = new ResponseStructure<>();
		    response.setStatuscode(200);
		    response.setMessage("SHOWS FOUND SUCCESSFULLY");
		    response.setData(shows);

		    return response;
		}
	
		// find shows by screen

		public ResponseEntity<ResponseStructure<List<Show>>> findShowsByScreen(
		        long theaterid, int screenno) {

		    Theater theater = theaterRepo.findById(theaterid)
		            .orElseThrow(() ->
		                new TheaterNotFoundException("THEATER NOT FOUND"));

		    List<Show> shows =
		            showRepo.findByTheater_IdAndScreenno(theaterid, screenno);

		    if (shows.isEmpty()) {
		        throw new ShowNotFoundException("NO SHOWS FOUND ON THIS SCREEN");
		    }

		    ResponseStructure<List<Show>> response = new ResponseStructure<>();
		    response.setStatuscode(HttpStatus.OK.value());
		    response.setMessage("SHOWS FOUND SUCCESSFULLY");
		    response.setData(shows);

		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		// get complete show details

		public ResponseEntity<ResponseStructure<Show>> getShowDetails(
		        long theaterid, long showid) {

		    Theater theater = theaterRepo.findById(theaterid)
		            .orElseThrow(() ->
		                new TheaterNotFoundException("THEATER NOT FOUND"));

		    Show show = showRepo.findById(showid)
		            .orElseThrow(() ->
		                new ShowNotFoundException("SHOW NOT FOUND"));

		    if (show.getTheater() == null ||
		            show.getTheater().getId() != theaterid) {
		        throw new ShowNotFoundException(
		                "SHOW DOES NOT BELONG TO THIS THEATER");
		    }

		    ResponseStructure<Show> response = new ResponseStructure<>();
		    response.setStatuscode(HttpStatus.OK.value());
		    response.setMessage("SHOW DETAILS FETCHED SUCCESSFULLY");
		    response.setData(show);

		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
	
}
