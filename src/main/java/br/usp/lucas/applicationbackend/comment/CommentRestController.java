package br.usp.lucas.applicationbackend.comment;

import br.usp.lucas.applicationbackend.comment.dto.CommentReadDto;
import br.usp.lucas.applicationbackend.comment.dto.CommentWriteDto;
import br.usp.lucas.applicationbackend.post.Post;
import br.usp.lucas.applicationbackend.post.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
An alternative to this endpoint would be to manage the comments as other methods in the "posts" endpoint, as explained below.
Doing so is also a nice way because it shows that comments are like an attribute of a post, always being associated to one.
In that case, CommentWriteDto would also not need the postId attribute.
 */
@RestController
@RequestMapping("comments")
public class CommentRestController {
    private final CommentRepository repository;
    private final PostRepository postRepository;

    public CommentRestController(CommentRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    /*
    Alternative: GET method with URL "/posts/{postId}/comments"
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<CommentReadDto> getAllByPostId(@RequestParam Integer postId) {
        final List<Comment> comments = repository.getAllByPostId(postId);

        final List<CommentReadDto> dtos = new ArrayList<>(comments.size());
        for (Comment comment : comments) {
            dtos.add(convertToReadDto(comment));
        }

        return dtos;
    }

    private CommentReadDto convertToReadDto(Comment entity) {
        final CommentReadDto dto = new CommentReadDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        dto.setEmail(entity.getEmail());

        return dto;
    }

    /*
    Alternative: POST method with URL "/posts/{postId}/comments"
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> create(@RequestBody CommentWriteDto dto) {
        final Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setBody(dto.getBody());
        comment.setEmail(dto.getEmail());

        final Optional<Post> postOptional = postRepository.findById(dto.getPostId());
        if (postOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post with ID " + dto.getPostId() + " not found");
        }

        comment.setPost(postOptional.get());

        repository.save(comment);
        return ResponseEntity.created(URI.create("/comments/" + comment.getId())).build();
    }

    /*
    Alternative: PUT method with URL "/posts/{postId}/comments/{id}". This would be kind of redundant, since we would
    need to check both the IDs of the post and the comment, but could be useful if we needed to ensure that the comment
    exists within the desired post.
     */
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody CommentWriteDto dto) {
        final Optional<Comment> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Comment existingComment = optional.get();
            existingComment.setTitle(dto.getTitle());
            existingComment.setBody(dto.getBody());
            existingComment.setEmail(dto.getEmail());

            final Optional<Post> postOptional = postRepository.findById(dto.getPostId());
            if (postOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post with ID " + dto.getPostId() + " not found");
            }

            existingComment.setPost(postOptional.get());

            repository.save(existingComment);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Alternative: DELETE method with URL "/posts/{postId}/comments/{id}". This would be kind of redundant, since we would
    need to check both the IDs of the post and the comment, but could be useful if we needed to ensure that the comment
    exists within the desired post.
     */
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        final Optional<Comment> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Comment comment = optional.get();
            repository.delete(comment);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
