package br.usp.lucas.applicationbackend.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    /*
    As we are going to need the User mapping, we can use the "join fetch"; it is similar to a SQL join, meaning "Bring
    me the posts along with the associated user for each of them". In SQL, it is translated to an "inner join".

    Note: We are using "join fetch" because the foreign key reference is required (not null); when it is optional or is
    a list, we must use "left join fetch", which is translated to a SQL "left join".
     */
    @Query("select p from Post p join fetch p.user")
    List<Post> findAllWithUser();

    @Query("select p from Post p join fetch p.user where p.id = :id")
    Optional<Post> findByIdWithUser(Integer id);
}
