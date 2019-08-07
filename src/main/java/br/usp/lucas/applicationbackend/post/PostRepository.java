package br.usp.lucas.applicationbackend.post;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    /*
    We wish to use a Query by Example, but method findAll will not fetch the user by default; as an alternative to using
    join fetch, we can use EntityGraph to specify that this method should fetch the user as we wish.
     */
    @Override
    @EntityGraph(attributePaths = "user")
    <S extends Post> List<S> findAll(Example<S> example, Sort sort);

    @Query("select p from Post p join fetch p.user where p.id = :id")
    Optional<Post> findByIdWithUser(Integer id);
}
