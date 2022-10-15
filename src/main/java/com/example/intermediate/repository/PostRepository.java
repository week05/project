package com.example.intermediate.repository;

import com.example.intermediate.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();

  List<Post> findAllByMember_Id(Long id);

  Optional<Post> findById(Long id);
}
