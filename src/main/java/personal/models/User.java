package personal.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class User {
    private String name;
    private String username;
    private String password;
    private List<String> roles;
}
