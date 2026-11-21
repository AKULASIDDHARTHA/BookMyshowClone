package com.alpha.theaterservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alpha.theaterservice.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShow_IdAndStatus(long showId, String status);
}