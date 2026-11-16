package com.alpha.theaterservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alpha.theaterservice.dto.CreateTheaterDto;
import com.alpha.theaterservice.dto.ResponseStructure;
import com.alpha.theaterservice.entity.Theater;
import com.alpha.theaterservice.exception.TheaterAlreadyExistWithContactException;
import com.alpha.theaterservice.repository.TheaterRepository;

@Service
public class TheaterService {

	@Autowired
	private TheaterRepository theaterRepo;

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

	// FIND THEATER BY ID
	public ResponseEntity<ResponseStructure<Theater>> findTheater(long id) {

		Optional<Theater> theaterOptional = theaterRepo.findById(id);

		ResponseStructure<Theater> responseStructure = new ResponseStructure<Theater>();

		if (theaterOptional.isPresent()) {

			Theater theater = theaterOptional.get();

			responseStructure.setStatuscode(HttpStatus.OK.value());

			responseStructure.setMessage("THEATER FOUND SUCCESSFULLY");

			responseStructure.setData(theater);

			return new ResponseEntity<ResponseStructure<Theater>>(responseStructure, HttpStatus.OK);
		}

		responseStructure.setStatuscode(HttpStatus.NOT_FOUND.value());

		responseStructure.setMessage("THEATER NOT FOUND");

		responseStructure.setData(null);

		return new ResponseEntity<ResponseStructure<Theater>>(responseStructure, HttpStatus.NOT_FOUND);
	}

	
	//Delete Theater
	public ResponseEntity<ResponseStructure<Theater>> deleteAccount(long id) {
		Optional<Theater> theaterOptional = theaterRepo.findById(id);

		ResponseStructure<Theater> responseStructure = new ResponseStructure<Theater>();

		if (theaterOptional.isPresent()) {

			Theater theater = theaterOptional.get();

			theaterRepo.delete(theater);

			responseStructure.setStatuscode(HttpStatus.OK.value());
			responseStructure.setMessage("THEATER DELETED SUCCESSFULLY");
			responseStructure.setData(null);

			return new ResponseEntity<ResponseStructure<Theater>>(responseStructure, HttpStatus.OK);
		}
		responseStructure.setStatuscode(HttpStatus.NOT_FOUND.value());

		responseStructure.setMessage("THEATER NOT FOUND");

		responseStructure.setData(null);

		return new ResponseEntity<ResponseStructure<Theater>>(responseStructure, HttpStatus.NOT_FOUND);
	}

}
