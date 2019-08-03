package br.usp.lucas.applicationbackend.post;

import br.usp.lucas.applicationbackend.post.dto.PostReadDto;
import br.usp.lucas.applicationbackend.post.dto.PostWriteDto;
import br.usp.lucas.applicationbackend.user.User;
import br.usp.lucas.applicationbackend.user.UserRepository;
import br.usp.lucas.applicationbackend.user.dto.UserReadDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("posts")
public class PostRestController {
    private final PostRepository repository;
    private final UserRepository userRepository;

    public PostRestController(PostRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<PostReadDto> getAll() {
        final List<Post> posts = repository.findAllWithUser();

        final List<PostReadDto> dtos = new ArrayList<>(posts.size());
        for (Post post : posts) {
            dtos.add(convertToReadDto(post));
        }

        return dtos;
    }

    private PostReadDto convertToReadDto(Post entity) {
        final PostReadDto dto = new PostReadDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());

        final User user = entity.getUser();

        final UserReadDto userDto = new UserReadDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        dto.setUser(userDto);

        return dto;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PostReadDto> getById(@PathVariable Integer id) {
        final Optional<Post> optional = repository.findByIdWithUser(id);

        final Optional<PostReadDto> dtoOptional = optional.map(this::convertToReadDto);
        return ResponseEntity.of(dtoOptional);
    }

    /*
    Rather than passing the Post as an entity as the payload, we are using a DTO (data transfer object) containing only
    the relevant fields and having the foreign key references being just an ID. This also has the advantage that we can
    add or modify fields of the Post class without modifying the required payload for this service.

    It is a nice practice to do the same for all request and response classes from web services; we could create a "ReadDto"
    for the getAll and getById methods, and also create read and write DTOs for the User web services.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> create(@RequestBody PostWriteDto dto) {
        final Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());

        //If we cannot find a User requested in the payload, we can return HTTP 404 (Not Found) and pass a message in the
        //response so that the client can know what went wrong.
        final Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + dto.getUserId() + " not found");
        }

        post.setUser(userOptional.get());

        repository.save(post);
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).build();
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody PostWriteDto dto) {
        final Optional<Post> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Post existingPost = optional.get();
            existingPost.setTitle(dto.getTitle());
            existingPost.setBody(dto.getBody());

            final Optional<User> userOptional = userRepository.findById(dto.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + dto.getUserId() + " not found");
            }

            existingPost.setUser(userOptional.get());

            repository.save(existingPost);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        final Optional<Post> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Post post = optional.get();
            repository.delete(post);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
