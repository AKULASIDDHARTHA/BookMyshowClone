
package com.alpha.theaterservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alpha.theaterservice.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

	Optional<Movie> findByTitleContainsIgnoreCase(String title);

}
