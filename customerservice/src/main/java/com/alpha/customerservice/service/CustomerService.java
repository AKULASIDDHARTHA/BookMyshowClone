package com.alpha.customerservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alpha.customerservice.dto.CreateCustomerDto;
import com.alpha.customerservice.dto.InitiatePayment;
import com.alpha.customerservice.dto.MovieDto;
import com.alpha.customerservice.dto.ResponseStructure;
import com.alpha.customerservice.dto.SeatDto;
import com.alpha.customerservice.dto.ShowDto;
import com.alpha.customerservice.entity.Customer;
import com.alpha.customerservice.entity.Ticket;
import com.alpha.customerservice.exception.CustomerAlreadyExistWithPhoneException;
import com.alpha.customerservice.exception.CustomerNotFoundException;
import com.alpha.customerservice.exception.SeatNotAvailableException;
import com.alpha.customerservice.exception.SeatNotFoundException;
import com.alpha.customerservice.exception.ShowNotFoundException;
import com.alpha.customerservice.exception.TicketNotFoundException;
import com.alpha.customerservice.repository.CustomerRepository;
import com.alpha.customerservice.repository.TicketRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private TicketRepository ticketRepo;

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

	    try {
	        ResponseEntity<ResponseStructure<List<MovieDto>>> response =
	                restTemplate.exchange(
	                        url,
	                        HttpMethod.GET,
	                        null,
	                        new ParameterizedTypeReference<ResponseStructure<List<MovieDto>>>() {}
	                );
	        return response.getBody().getData();
	    } catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) {

	        throw new RuntimeException("MOVIES NOT FOUND");
	    }
	}

	public List<SeatDto> selectSeat(long showId) {

	    String url = "http://localhost:8080/shows/seats/available?showId=" + showId;
	    try {

	        ResponseEntity<List<SeatDto>> response =
	                restTemplate.exchange(
	                        url,
	                        HttpMethod.GET,
	                        null,
	                        new ParameterizedTypeReference<List<SeatDto>>() {}
	                );

	        return response.getBody();
	    } catch (HttpClientErrorException.NotFound e) {

	        throw new ShowNotFoundException("SHOW NOT FOUND");
	    }
	}
	

public Ticket bookSeats(long customerId, long showId, List<String> seats) {

    // 1. Find customer
    Customer customer = customerRepo.findById((int) customerId)
            .orElseThrow(() ->
                new CustomerNotFoundException("CUSTOMER NOT FOUND"));

    try {

        // 2. Get show details
        String showUrl = "http://localhost:8080/theater/findshow?id=" + showId;

        ResponseEntity<ResponseStructure<ShowDto>> showResponse =
                restTemplate.exchange(
                        showUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseStructure<ShowDto>>() {}
                );

        ShowDto show = showResponse.getBody().getData();

        // 3. Lock selected seats
        String seatUrl = "http://localhost:8080/shows/seats/lock?showId=" + showId;

        ResponseEntity<List<SeatDto>> seatResponse =
                restTemplate.exchange(
                        seatUrl,
                        HttpMethod.POST,
                        new HttpEntity<>(seats),
                        new ParameterizedTypeReference<List<SeatDto>>() {}
                );

        List<SeatDto> lockedSeats = seatResponse.getBody();

        // 4. Create ticket
        Ticket ticket = new Ticket(
                customer.getName(),
                String.join(",", seats),
                show.getTime(),
                show.getTitle(),
                show.getScreenno(),
                show.getPrice() * seats.size(),
                show.getDate(),
                show.getLanguage(),
                LocalDateTime.now()
        );

        // 5. Save ticket
        return ticketRepo.save(ticket);

    } catch (HttpClientErrorException.Conflict e) {

        throw new SeatNotAvailableException(
                "SELECTED SEAT IS NOT AVAILABLE");

    } catch (HttpClientErrorException.NotFound e) {

        throw new SeatNotFoundException(
                "SELECTED SEATS NOT FOUND");
    }
}
	public Ticket bookTicketPayment(long ticketId) {

	    Ticket ticket = ticketRepo.findById((int) ticketId)
	            .orElseThrow(() ->
	                new TicketNotFoundException("TICKET NOT FOUND"));

	    // Simulated payment
	    boolean paymentSuccess = true;

	    if (paymentSuccess) {
	        ticket.setPaymentStatus(true);
	    }

	    return ticketRepo.save(ticket);
	}
}