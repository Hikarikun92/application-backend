package br.usp.lucas.applicationbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

//Notice that now we don't need to implement this interface, nor declare it as @Repository and we don't even need to use
//the EntityManager; Spring will detect anything that is necessary and implement it for us
public interface UserRepository extends JpaRepository<User, Integer> {
}
