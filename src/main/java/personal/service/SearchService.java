package personal.service;

import personal.litespring.annotation.Autowired;
import personal.litespring.annotation.Component;
import personal.models.Product;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchService {

    @Autowired
    private ProductService productService;

    public List<Product> search(String name) {
        List<Product> products = productService.getAllProducts();
        return products.stream().filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }
}
