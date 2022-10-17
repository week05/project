package com.example.intermediate.repository;

import com.example.intermediate.domain.Category;
import com.example.intermediate.domain.Post;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findByCategory(Category category);
  List<Post> findAllByMember_Id(Long id);
  Optional<Post> findById(Long id);
  Page<Post> findAll(Pageable pageable);

}
