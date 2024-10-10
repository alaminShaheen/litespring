package personal.models.dto;

import lombok.Data;
import personal.models.Product;

import java.util.List;

@Data
public class SearchResponse {
    private List<Product> products;
}
