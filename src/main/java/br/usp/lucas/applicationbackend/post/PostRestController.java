package br.usp.lucas.applicationbackend.post;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("posts")
public class PostRestController {
    private final PostRepository repository;

    public PostRestController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Post> getAll() {
        return repository.findAllWithUser();
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> getById(@PathVariable Integer id) {
        final Optional<Post> optional = repository.findByIdWithUser(id);
        return ResponseEntity.of(optional);
    }

    /*
    Problem: we need to pass the User as an object in the payload, although only the ID is necessary as a reference;
    having the entire User entity is not the responsibility of this web service.

    We could pass an object having only the ID set (e.g. {"id": 1}); this is kind of unnecessary and could be replaced
    by a simple number. Also, having an object, we could even pass some weird info, like a wrong name or e-mail, to the
    user; although this would not modify the Users table, it would be a strange and inconsistent behavior.

    In case we pass an ID that does not exist in the Users table, this method would also throw an Internal Server Error.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> create(@RequestBody Post post) {
        post.setId(null);

        repository.save(post);
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).build();
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Post post) {
        final Optional<Post> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Post existingPost = optional.get();
            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());
            existingPost.setUser(post.getUser());

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
