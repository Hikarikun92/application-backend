package br.usp.lucas.applicationbackend.post;

import br.usp.lucas.applicationbackend.post.dto.PostReadDto;
import br.usp.lucas.applicationbackend.post.dto.PostWriteDto;
import br.usp.lucas.applicationbackend.user.User;
import br.usp.lucas.applicationbackend.user.UserFilter;
import br.usp.lucas.applicationbackend.user.UserRepository;
import br.usp.lucas.applicationbackend.user.dto.UserReadDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
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

    /*
    To reference the User attributes in the filter, use it like "user.id", "user.name" etc., where "user" is the name of
    the property in the PostFilter. If the getter and setter for it were called "getUserFilter" and "setUserFilter", for
    example, the parameter to use would have to match it (e.g. "userFilter.id", "userFilter.name" etc.).

    Also, notice that Sort here can reference the User attributes as well: just pass it as "?sort=user.name", for example!
    However, in that case, if must match the name of the field in entity Post instead of the property of the filter.
    As a good practice, we should make both the properties in the entity and in the filter have the same names.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<PostReadDto> getAll(PostFilter filter, Sort sort) {
        final Post examplePost = new Post();
        examplePost.setTitle(filter.getTitle());
        examplePost.setBody(filter.getBody());

        final UserFilter user = filter.getUser();
        if (user != null) {
            final User exampleUser = new User();
            exampleUser.setId(user.getId());
            exampleUser.setName(user.getName());
            exampleUser.setUsername(user.getUsername());
            exampleUser.setEmail(user.getEmail());

            examplePost.setUser(exampleUser);
        }

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        final Example<Post> example = Example.of(examplePost, matcher);
        final List<Post> posts = repository.findAll(example, sort);

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
