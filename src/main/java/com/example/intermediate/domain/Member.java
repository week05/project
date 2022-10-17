package com.example.intermediate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @OneToMany(mappedBy = "member",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
  @JsonBackReference
  private List<Post> posts;

  @OneToMany(mappedBy = "member",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  @JsonBackReference
  private List<Comment> comments;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Member member = (Member) o;
    return id != null && Objects.equals(id, member.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
    return passwordEncoder.matches(password, this.password);
  }
}
