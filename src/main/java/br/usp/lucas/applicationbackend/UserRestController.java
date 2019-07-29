package br.usp.lucas.applicationbackend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserRestController {
    //Although we are saying that the dataSource should be passed as an argument to this class's constructor, we will not
    //do it manually; Spring will automatically create this class for us and pass the dataSource properly. This is called
    //"Dependency injection". Another way of doing this would be to not define the constructor, remove the "final" from
    //this field and use annotation "@Autowired" over it, for example: @Autowired private DataSource dataSource;
    private final DataSource dataSource;

    public UserRestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //This method should be called when the client sends an HTTP GET pointing to the same URL of the controller
    //(i.e. http://localhost:8080/users), and will produce the response in JSON format with UTF-8 encoding
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<User> getAll() throws SQLException {
        //This is the "root way" (JDBC) of connecting to the database and converting the data from the tables into Java objects
        try (Connection connection = dataSource.getConnection()) {
            final ResultSet resultSet = connection.createStatement().executeQuery("select id, name, username, email from user order by id");

            final List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                final int id = resultSet.getInt(1);
                final String name = resultSet.getString(2);
                final String username = resultSet.getString(3);
                final String email = resultSet.getString(4);

                final User user = new User(id, name, username, email);
                result.add(user);
            }

            return result;
        }
    }
}
