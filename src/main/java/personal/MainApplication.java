package personal;

import personal.litespring.ApplicationContext;
import personal.litespring.LiteSpringApplication;
import personal.litespring.annotation.PackageScan;
import personal.models.Product;
import personal.service.ProductService;
import personal.service.SearchService;

import java.util.List;

@PackageScan(scanPackages = {"personal"})
public class MainApplication {

    public static void main(String[] args) throws Exception {

        ApplicationContext applicationContext = LiteSpringApplication.run(MainApplication.class);

        ProductService productService = (ProductService) applicationContext.getBean(ProductService.class);
        SearchService searchService = (SearchService) applicationContext.getBean(SearchService.class);

        Product product1 = new Product();
        product1.setName("iPhone 14");
        productService.addProduct(product1);

        Product product2 = new Product();
        product2.setName("iPhone 16");
        productService.addProduct(product2);

        Product product3 = new Product();
        product3.setName("Samsung Galaxy S24");
        productService.addProduct(product3);

        List<Product> productList = searchService.search("iphone");
        for(Product product : productList) {
            System.out.println("product = " + product);
        }

    }
}


