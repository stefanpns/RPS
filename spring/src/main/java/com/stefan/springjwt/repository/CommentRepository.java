package com.stefan.springjwt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stefan.springjwt.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  
  Page<Comment> findByTutid(Long tutid, Pageable pageable);
  List<Comment> deleteByTutid(Long tutid);

}
