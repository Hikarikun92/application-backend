package br.usp.lucas.applicationbackend.user;

import org.springframework.http.HttpStatus;
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

    /*
    Problems with this method:
    - We can pass an ID the body. If a user with that ID exists, it will be overwritten, which is not the intention of this method.
    - If the ID does not exist, a new object will be created, but the ID returned in the location header might not be the correct one.

    How can we solve those problems?
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> create(@RequestBody User user) {
        repository.save(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
    }

    /*
    Problems with this method:
    - The ID passed in the URL must match the one in the payload, otherwise we will try to edit an object but will overwrite another.
    - If the ID does not exist, a new object will be created. Is that correct for this service?
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void update(@PathVariable Integer id, @RequestBody User user) {
        repository.save(user);
    }

    /*
     Problem with this method: what if the ID does not exist? The application is throwing an "Internal Server Error"!
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
