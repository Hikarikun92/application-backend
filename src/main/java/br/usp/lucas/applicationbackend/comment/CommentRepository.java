package br.usp.lucas.applicationbackend.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> getAllByPostId(Integer postId);
}
