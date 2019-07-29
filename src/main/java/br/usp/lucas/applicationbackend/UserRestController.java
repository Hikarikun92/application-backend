package br.usp.lucas.applicationbackend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserRestController {
    //Although we are saying that the EntityManager should be passed as an argument to this class's constructor, we will not
    //do it manually; Spring will automatically create this class for us and pass the entityManager properly. This is called
    //"Dependency injection". Another way of doing this would be to not define the constructor, remove the "final" from
    //this field and use annotation "@Autowired" over it, for example: @Autowired private EntityManager entityManager;
    private final EntityManager entityManager;

    public UserRestController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAll() {
        final List<User> users = entityManager.createQuery("select u from User u order by u.id", User.class)
                .getResultList();
        return users;
    }
}
