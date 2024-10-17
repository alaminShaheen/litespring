package personal.controller;

import personal.litespring.annotation.*;
import personal.models.Product;
import personal.models.dto.AddProductRequest;
import personal.models.dto.AddProductResponse;
import personal.models.dto.SearchResponse;
import personal.service.ProductService;
import personal.service.SearchService;

import java.util.List;

@Component
@RestController
@RequestMapping(basePath = "/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SearchService searchService;

    @PostMapping(basePath = "/product")
    @ResponseBody
    public AddProductResponse addProduct(@RequestBody AddProductRequest request) {
        System.out.println("Hello World");
        Product product = new Product();
        product.setName(request.getName());

        String id = productService.addProduct(product);

        AddProductResponse addProductResponse = new AddProductResponse();
        addProductResponse.setId(id);

        return addProductResponse;
    }

    @GetMapping(basePath = "/product/{id}")
    @ResponseBody
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }

    @GetMapping(basePath = "/products")
    @ResponseBody
    public SearchResponse search(@RequestParam("query") String query) {
        List<Product> productList = searchService.search(query);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(productList);
        return searchResponse;
    }
}
