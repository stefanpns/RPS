package com.stefan.springjwt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stefan.springjwt.models.Shoelast;

@Repository
public interface ShoelastRepository extends JpaRepository<Shoelast, Long> {
  Page<Shoelast> findByShoesize(int shoesize, Pageable pageable);
}
