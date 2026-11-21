package com.alpha.customerservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alpha.customerservice.dto.CreateCustomerDto;
import com.alpha.customerservice.dto.MovieDto;
import com.alpha.customerservice.dto.ResponseStructure;
import com.alpha.customerservice.dto.SeatDto;
import com.alpha.customerservice.entity.Customer;
import com.alpha.customerservice.exception.CustomerAlreadyExistWithPhoneException;
import com.alpha.customerservice.exception.CustomerNotFoundException;
import com.alpha.customerservice.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    //create
    public ResponseEntity<ResponseStructure<Customer>> createCustomerAccount(
            CreateCustomerDto createCustomerDto) {

        Optional<Customer> customerOptional =
                customerRepo.findByPhone(createCustomerDto.getPhone());

        System.out.println("Received Phone: " + createCustomerDto.getPhone());

        System.out.println("Is Phone Present: " + customerOptional.isPresent());

        if (customerOptional.isPresent()) {
            throw new CustomerAlreadyExistWithPhoneException();
        }

        Customer newCustomer = new Customer();

        newCustomer.setName(createCustomerDto.getName());
        newCustomer.setMail(createCustomerDto.getMail());
        newCustomer.setPhone(createCustomerDto.getPhone());
        newCustomer.setAge(createCustomerDto.getAge());
        newCustomer.setGender(createCustomerDto.getGender());

        Customer savedCustomer = customerRepo.save(newCustomer);

        ResponseStructure<Customer> responseStructure = new ResponseStructure<Customer>();

        responseStructure.setStatuscode(HttpStatus.CREATED.value());

        responseStructure.setMessage("NEW CUSTOMER CREATED SUCCESSFULLY");

        responseStructure.setData(savedCustomer);

        return new ResponseEntity<ResponseStructure<Customer>>(
                responseStructure, HttpStatus.CREATED);
    }
    
    //FIndCustomer
    public ResponseEntity<ResponseStructure<Customer>> findCustomerById(int id) {

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("CUSTOMER NOT FOUND"));

        ResponseStructure<Customer> responseStructure = new ResponseStructure<>();

        responseStructure.setStatuscode(HttpStatus.OK.value());
        responseStructure.setMessage("CUSTOMER FOUND SUCCESSFULLY");
        responseStructure.setData(customer);

        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }
    
	//delete
	public ResponseEntity<ResponseStructure<Customer>> deleteCustomerById(int id) {
		 Customer customer = customerRepo.findById(id)
	                .orElseThrow(() -> new CustomerNotFoundException("CUSTOMER NOT FOUND"));
		 ResponseStructure<Customer> responseStructure = new ResponseStructure<>();
		 	customerRepo.delete(customer);
	        responseStructure.setStatuscode(HttpStatus.OK.value());
	        responseStructure.setMessage("CUSTOMER DELETED SUCCESSFULLY");
	        responseStructure.setData(null);

	        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
		}

	@Autowired
	private RestTemplate restTemplate;

	public List<MovieDto> findMovieByName(String moviename) {

	    String url = "http://localhost:8080/findmovie?moviename=" + moviename;

	    ResponseEntity<ResponseStructure<List<MovieDto>>> response =
	            restTemplate.exchange(
	                    url,
	                    HttpMethod.GET,
	                    null,
	                    new ParameterizedTypeReference<ResponseStructure<List<MovieDto>>>() {}
	            );

	    return response.getBody().getData();
	}

	public List<SeatDto> selectSeat(long showId) {

	    String url = "http://localhost:8080/shows/seats/available?showId=" + showId;

	    ResponseEntity<List<SeatDto>> response =
	            restTemplate.exchange(
	                    url,
	                    HttpMethod.GET,
	                    null,
	                    new ParameterizedTypeReference<List<SeatDto>>() {}
	            );

	    return response.getBody();
	}
	}