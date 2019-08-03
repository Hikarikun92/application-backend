package br.usp.lucas.applicationbackend.user;

import br.usp.lucas.applicationbackend.user.dto.UserReadDto;
import br.usp.lucas.applicationbackend.user.dto.UserWriteDto;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserReadDto> getAll() {
        final List<User> users = repository.findAll();

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
