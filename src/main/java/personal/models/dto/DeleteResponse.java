package personal.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DeleteResponse {
    private boolean success;
}
