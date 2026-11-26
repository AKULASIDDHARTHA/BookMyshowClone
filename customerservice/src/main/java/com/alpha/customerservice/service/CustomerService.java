package com.alpha.customerservice.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alpha.customerservice.dto.ConfirmTicketDto;
import com.alpha.customerservice.dto.CreateCustomerDto;
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

import tools.jackson.databind.ObjectMapper;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private TicketRepository ticketRepo;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    

	@Autowired
	private RestTemplate restTemplate;

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
	
	public ConfirmTicketDto bookSeats(long customerId, long showId, List<String> seats) {
	    Customer customer = customerRepo.findById((int) customerId)
	            .orElseThrow(() ->
	                new CustomerNotFoundException("CUSTOMER NOT FOUND"));
	    String showUrl = "http://localhost:8080/theater/findshow?id=" + showId;
	    ResponseEntity<ResponseStructure<ShowDto>> showResponse =
	            restTemplate.exchange(
	                    showUrl,
	                    HttpMethod.GET,
	                    null,
	                    new ParameterizedTypeReference<ResponseStructure<ShowDto>>() {}
	            );
	    ShowDto show = showResponse.getBody().getData();
	    String value = "customer:" + customerId;
	    List<String> lockedKeys = new ArrayList<>();
	    try {
	        // 1. Freeze seats in Redis
	        for (String seat : seats) {

	            String key = getSeatKey(showId, seat);

	            Boolean result = redisTemplate.opsForValue()
	                    .setIfAbsent(key, value, Duration.ofSeconds(30));

	            if (!Boolean.TRUE.equals(result)) {
	                throw new SeatNotAvailableException(
	                        seat + " IS ALREADY RESERVED");
	            }

	            lockedKeys.add(key);
	        }

	        // 2. Create confirmation DTO
	        ConfirmTicketDto dto = new ConfirmTicketDto();
	        dto.setMoviename(show.getTitle());
	        dto.setDate(show.getDate());
	        dto.setTime(show.getTime());
	        dto.setLanguage(show.getLanguage());
	        dto.setNoOfSeats(seats.size());
	        dto.setSeatno(String.join(",", seats));
	        dto.setTicketPrice(show.getPrice());
	        // Temporary value; replace with your actual convenience fee
	        dto.setConvenienceFees(0.0);
	        dto.setTheatreName("Not Available");
	        dto.setScreenNo(show.getScreenno());
	        double totalPrice =
	                (show.getPrice() * seats.size()) + dto.getConvenienceFees();
	        dto.setTotalPrice(totalPrice);
	        dto.setCustomer(customer.getName());
	        dto.setCustomerNo(String.valueOf(customer.getPhone()));
	        
	        dto.setShowId(showId);
	        // 3. Store confirmation details in Redis
	        String confirmKey = "confirmticket:" + customerId;
	        String json = objectMapper.writeValueAsString(dto);
	        redisTemplate.opsForValue().set(
	                confirmKey,
	                json,
	                Duration.ofSeconds(30)
	        );
	        return dto;
	    } catch (Exception e) {

	        // Release seats if confirmation creation fails
	        for (String key : lockedKeys) {
	            String existingValue = redisTemplate.opsForValue().get(key);

	            if (value.equals(existingValue)) {
	                redisTemplate.delete(key);
	            }
	        }
	        throw new RuntimeException("TICKET CONFIRMATION FAILED: " + e.getMessage());
	    }
	}
	
	
	private String getSeatKey(long showId, String seatNo) {
	    return "lockseat:" + showId + ":" + seatNo.toUpperCase();
	}
	
	public List<SeatDto> availableSeats(long showId) {

	    String url =
	        "http://localhost:8080/shows/seats/available?showId=" + showId;

	    ResponseEntity<List<SeatDto>> response =
	        restTemplate.exchange(
	            url,
	            HttpMethod.GET,
	            null,
	            new ParameterizedTypeReference<List<SeatDto>>() {}
	        );

	    List<SeatDto> seats = response.getBody();

	    List<SeatDto> availableSeats = new ArrayList<>();

	    for (SeatDto seat : seats) {

	        String key = getSeatKey(showId, seat.getSeatno());

	        // Check whether seat is frozen in Redis
	        String value = redisTemplate.opsForValue().get(key);

	        if (value == null) {
	            availableSeats.add(seat);
	        }
	    }

	    return availableSeats;
	}
	
	public Ticket confirmTicketAndPay(long custId) {

	    // 1. Find confirmation details in Redis
	    String confirmKey = "confirmticket:" + custId;

	    String json = redisTemplate.opsForValue().get(confirmKey);

	    if (json == null) {
	        throw new RuntimeException(
	                "TICKET CONFIRMATION EXPIRED OR NOT FOUND");
	    }

	    try {

	        // 2. Convert JSON into ConfirmTicketDto
	        ConfirmTicketDto dto =
	                objectMapper.readValue(json, ConfirmTicketDto.class);

	        // 3. Check whether all selected seats are still frozen
	        String[] seats = dto.getSeatno().split(",");

	        for (String seat : seats) {

	            String key = getSeatKey(dto.getShowId(), seat);

	            String value = redisTemplate.opsForValue().get(key);

	            if (!("customer:" + custId).equals(value)) {
	                throw new SeatNotAvailableException(
	                        seat + " RESERVATION EXPIRED");
	            }
	        }

	        // 4. Simulated third-party payment
	        boolean payment = true;

	        if (payment) {

	            // 5. Create final Ticket
	            Ticket ticket = new Ticket(
	                    dto.getCustomer(),
	                    dto.getSeatno(),
	                    dto.getTime(),
	                    dto.getMoviename(),
	                    dto.getScreenNo(),
	                    dto.getTotalPrice(),
	                    dto.getDate(),
	                    dto.getLanguage(),
	                    LocalDateTime.now(),
	                    true
	            );

	            Ticket savedTicket = ticketRepo.save(ticket);

	            // 6. Keep the seats frozen after successful payment
	            for (String seat : seats) {
	                String key =getSeatKey(dto.getShowId(), seat);
	                redisTemplate.persist(key);
	            }

	            // 7. Remove confirmation details from Redis
	            redisTemplate.delete(confirmKey);

	            return savedTicket;
	        }

	        throw new RuntimeException("PAYMENT FAILED");

	    } catch (SeatNotAvailableException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new RuntimeException(
	                "PAYMENT PROCESSING FAILED: " + e.getMessage());
	    }
	}
}