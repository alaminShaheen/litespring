package personal.models.dto;

import lombok.Builder;
import lombok.Data;
import personal.models.User;

@Data
@Builder
public class RegisterResponse {
    private User user;
}
