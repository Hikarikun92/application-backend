package br.usp.lucas.applicationbackend.user;

import br.usp.lucas.applicationbackend.user.dto.UserReadDto;
import br.usp.lucas.applicationbackend.user.dto.UserWriteDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class UserRestController {
    private final UserRepository repository;

    public UserRestController(UserRepository repository) {
        this.repository = repository;
    }

    /*
    We will use a "Query by example". We could, instead, define separate methods in our Repository class and call them
    depending on which values were set, but that can lead to some extremely complex conditions depending on the number
    of filters we want to use. Another option is the "Criteria query", used under the hood by Spring; but that is also
    really complex to learn from the beginning. The easiest way here is to use the Example queries.

    For sorting, we are using Spring's Sort class; to use it, specify in the URL the field(s) you wish to use and (optionally)
    the direction of the sort (default: ascending). For example: "...?sort=name&sort=username,desc" will use the name ascending
    and, if two or more of them are equal, it will use the username descending.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserReadDto> getAll(UserFilter filter, Sort sort) {
        //First, we must define an entity with all values null by default and then set only the ones we want to search
        //for (in our case, "name", "username" and "email"). The non-null fields will be the ones used for the search.
        //If all the fields are null, no filtering will be performed (returning all entities).
        final User exampleUser = new User();
        //Note: we are ignoring the user's ID possibly passed as parameter because we could call /users/{id} instead, but
        //feel free to set it if you think it is necessary.
        exampleUser.setName(filter.getName());
        exampleUser.setUsername(filter.getUsername());
        exampleUser.setEmail(filter.getEmail());

        //Now, we must define a matcher. We can define that we wish one matching ALL the non-null properties of the user
        //or ANY of them; the default one is ALL.
        final ExampleMatcher matcher = ExampleMatcher.matching()
                //The default String matcher is "EXACT", meaning the properties must contain the exact values we passed;
                //however, we wish that they just "contain" the filters so that we can look by just a part of the value
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        //Now, create an Example object and pass it to the Repository
        final Example<User> example = Example.of(exampleUser, matcher);
        final List<User> users = repository.findAll(example, sort);

        final List<UserReadDto> dtos = new ArrayList<>(users.size());
        for (User user : users) {
            dtos.add(convertToReadDto(user));
        }

        return dtos;
    }

    private UserReadDto convertToReadDto(User entity) {
        final UserReadDto dto = new UserReadDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());

        return dto;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserReadDto> getById(@PathVariable Integer id) {
        final Optional<User> optional = repository.findById(id);

        final Optional<UserReadDto> dtoOptional = optional.map(this::convertToReadDto);
        return ResponseEntity.of(dtoOptional);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> create(@RequestBody UserWriteDto dto) {
        final User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        repository.save(user); //The ID will be set in the same object in case we are creating a new row
        return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody UserWriteDto dto) {
        final Optional<User> optional = repository.findById(id); //Ensure the object exists
        if (optional.isPresent()) {
            //If the object exists, set the values coming from the payload to it
            final User existingUser = optional.get();
            existingUser.setName(dto.getName());
            existingUser.setUsername(dto.getUsername());
            existingUser.setEmail(dto.getEmail());

            repository.save(existingUser);

            //Return an empty response
            return ResponseEntity.noContent().build();
        } else {
            //If the object does not exist, do nothing and return a 404 Not Found error
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        final Optional<User> optional = repository.findById(id); //Ensure the object exists
        if (optional.isPresent()) {
            //If the object exists, delete it
            final User user = optional.get();
            repository.delete(user);

            //Return an empty response
            return ResponseEntity.noContent().build();
        } else {
            //If the object does not exist, do nothing and return a 404 Not Found error
            return ResponseEntity.notFound().build();
        }
    }
}
