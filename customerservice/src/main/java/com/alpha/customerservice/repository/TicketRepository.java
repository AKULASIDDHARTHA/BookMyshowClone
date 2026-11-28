
package com.alpha.customerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alpha.customerservice.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByShowId(long showId);

}
