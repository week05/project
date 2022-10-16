package com.example.intermediate.repository;

import com.example.intermediate.domain.Likes;

import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndPost(Member member, Post post);
    List<Likes>findByPost(Post post);


    List<Likes> findAllByMember(Member member);
}
