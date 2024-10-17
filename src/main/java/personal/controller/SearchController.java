package personal.controller;

import personal.litespring.annotation.*;
import personal.models.Product;
import personal.models.dto.SearchResponse;
import personal.service.SearchService;

@Component
@RestController
@RequestMapping(basePath = "/api/search")
public class SearchController {
    @Autowired
    private ProductController productController;
    @Autowired
    private SearchService searchService;

    @GetMapping(basePath = "/product/{id}")
    public Product searchProduct(@PathVariable("id") String id) {
        return productController.getProduct(id);
    }

    @GetMapping(basePath = "/products")
    public SearchResponse searchProducts(@RequestParam("query") String query) {
        return productController.search(query);
    }
}
