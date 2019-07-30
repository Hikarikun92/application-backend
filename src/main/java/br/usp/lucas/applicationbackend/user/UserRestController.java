package br.usp.lucas.applicationbackend.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class UserRestController {
    private final UserRepository repository;

    public UserRestController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        final Optional<User> optional = repository.findById(id);
        return ResponseEntity.of(optional);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> create(@RequestBody User user) {
        user.setId(null); //Ensure no existing object will be overwritten and the ID will be correctly set upon saving

        repository.save(user); //The ID will be set in the same object in case we are creating a new row
        return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody User user) {
        final Optional<User> optional = repository.findById(id); //Ensure the object exists
        if (optional.isPresent()) {
            //If the object exists, set the values coming from the payload to it and ignore a possibly wrong ID
            final User existingUser = optional.get();
            existingUser.setName(user.getName());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());

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
