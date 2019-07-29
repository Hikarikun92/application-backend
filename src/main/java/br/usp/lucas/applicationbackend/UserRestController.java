package br.usp.lucas.applicationbackend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserRestController {
    //This method should be called when the client sends an HTTP GET pointing to the same URL of the controller
    //(i.e. http://localhost:8080/users), and will produce the response in JSON format with UTF-8 encoding
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAll() {
        final User exampleUser1 = new User(1, "User 1", "user1", "user1@example.com");
        final User exampleUser2 = new User(2, "Lucas", "lucas", "lucas_jose_92@hotmail.com");
        final User exampleUser3 = new User(3, "Fernando", "fernando", null);

        return List.of(exampleUser1, exampleUser2, exampleUser3);
    }
}
