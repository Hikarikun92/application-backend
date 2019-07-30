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

    /*
    Problem: we are unable to call this web service and "get by ID"; Hibernate throws what we call a "lazy initialization exception".
    This happens because we have already exited the database transaction (only the Repository class uses it for now) and
    the User attribute was set to a proxy and cannot be fetched now, as we have specified it as "Lazy" in the Post entity.

    A solution for this would be to make the mapping Post -> User eager; however, this will make the Users table be queried
    as well, and when used without care, can ruin the application performance.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Post> getAll() {
        return repository.findAll();
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Post> getById(@PathVariable Integer id) {
        final Optional<Post> optional = repository.findById(id);
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
