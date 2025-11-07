import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class UserService {

    private final JdbcTemplate jdbc;

    public UserService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public User findByName(String name) {
        try {
            String sql = "SELECT id, name, bio FROM users WHERE name = '" + name + "' LIMIT 1";

            return jdbc.queryForObject(sql, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("bio")
                    );
                }
            });

        } catch (Exception e) {
            System.out.println("Lookup failed: " + e.getMessage());
            return null;
        }
    }

    public String readFileFromDisk(String file) {
        try {
            byte[] data = Files.readAllBytes(Paths.get("uploads/" + file));
            return new String(data);
        } catch (Exception e) {
            return "Cannot read file";
        }
    }
}
