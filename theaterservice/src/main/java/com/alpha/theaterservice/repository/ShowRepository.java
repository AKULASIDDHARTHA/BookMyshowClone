
package com.alpha.theaterservice.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.theaterservice.entity.Show;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    Optional<Show> findByDateAndTimeAndScreennoAndTheater_Id(
            LocalDate date,
            LocalTime time,
            int screenno,
            long theaterId);
    List<Show> findByTitleContainingIgnoreCaseAndLanguageIgnoreCaseAndDate(
            String title,
            String language,
            LocalDate date);
    
    List<Show> findByTheater_IdAndDate(long theaterId, LocalDate date);
    
    List<Show> findByTheater_IdAndTitleContainingIgnoreCase(
            long theaterId, String title);
}
