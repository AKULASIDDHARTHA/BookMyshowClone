package com.alpha.customerservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alpha.customerservice.dto.CreateCustomerDto;
import com.alpha.customerservice.dto.MovieDto;
import com.alpha.customerservice.dto.ResponseStructure;
import com.alpha.customerservice.dto.SeatDto;
import com.alpha.customerservice.entity.Customer;
import com.alpha.customerservice.service.CustomerService;
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerservice;

    @PostMapping("/customer/createAccount")
    public ResponseEntity<ResponseStructure<Customer>> create(
            @RequestBody CreateCustomerDto c) {

        System.out.println("Customer Registered");

        return customerservice.createCustomerAccount(c);
    }
    

    @GetMapping("/customer/findById")
    public ResponseEntity<ResponseStructure<Customer>> findById(@RequestParam int id) {

        return customerservice.findCustomerById(id);
    }
    
    @DeleteMapping("/customer/deleteById")
    public ResponseEntity<ResponseStructure<Customer>> deleteById(
            @RequestParam int id) {

        return customerservice.deleteCustomerById(id);
    }
    
    @GetMapping("/customer/findmovie")
    public List<MovieDto> findMovieByName(@RequestParam String moviename){
    	return customerservice.findMovieByName(moviename);
    }
    
    @GetMapping("/customer/selectseat")
    public ResponseEntity<List<SeatDto>> selectSeat(
        @RequestParam long showId) {
    List<SeatDto> seats = customerservice.selectSeat(showId);
    return ResponseEntity.ok(seats);
}

}
