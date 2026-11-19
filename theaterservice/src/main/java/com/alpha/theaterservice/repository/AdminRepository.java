package com.alpha.theaterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alpha.theaterservice.entity.Theater;

public interface AdminRepository extends JpaRepository<Theater, Long>{

}
