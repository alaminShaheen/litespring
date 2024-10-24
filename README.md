# LiteSpring Framework

LiteSpring is a lightweight, custom-built Java framework inspired by Java Spring Boot framework. This framework aims to provide a minimalistic solution for building REST APIs, handling routing, request mapping, and simple dependency injection, with support for annotations like RestController, GetMapping, and custom method-level annotations such as Authentication.

## Table of Contents
- [Features](#features)
- [Getting Started](#getting-started)
- [Usage](#usage)
  - [RestControllers and Mappings](#restcontrollers-and-mappings)
  - [Authentication](#authentication)
  - [Path Variables](#path-variables)
  - [Custom Filters](#custom-filters)
- [Annotations](#annotations)
- [Contributing](#contributing)
- [License](#license)




## Features

- **RestController & RequestMapping**: Simple mapping of HTTP requests for both controllers and controller methods using custom annotations.
- **Dynamic URL Path Variables**: Support for routes with dynamic URL segments such as **/api/products/{id}**.
- **Custom Annotations**: Implement and handle method-level annotations like
- **Request Filtering**: Manage pre-processing of requests, such as authentication, through filters.
- **Annotation-Based Configuration**: Lightweight configuration using annotations instead of XML.


## Getting Started

### Prerequisites
- Java 8+
- Apache Tomcat (Embedded)
- Maven (for dependency management)

### Installing

Clone the repository:
```
git clone https://github.com/alaminShaheen/litespring
cd litespring
```

Compile the project using Maven:
```
mvn clean install
```

Running the Application
To run the application, initialize the embedded Tomcat server and start handling requests:
```
public static void main(String[] args) {
    LiteSpringApplication.run(MainApplication.class, args);
}
```

## Annotations
Annotations are used to define the behavior of various components, services, and configurations in a declarative manner. They help simplify the setup and management of dependencies, routing, security, and more, without requiring extensive XML or configuration files. Annotations can be used with classes, methods, method parameters and even in attributes. These are the annotations that are currently supported in **LiteSpring**:

- **@Authenticated** for securing API endpoints.
- **@Autowired** to resolve and inject collaborating beans into other beans.
- **@Component** to detect custom beans automatically.
- **@GetMapping** to map HTTP GET requests onto specific handler methods.
- **@PackageScan** to recursively scan all classes within the defined package(s).
- **@PathVariable** for handling extraction of values from URI path.
- **@PostMapping** to map HTTP POST requests onto specific handler methods.
- **@RequestBody** maps the HttpRequest body to a request data transfer object to enable deserializing.
- **@RequestMapping** to map web requests to Spring Controller methods.
- **@RequestParam** for handling extraction of values from query string.
- **@ResponseBody** to serialize response object in JSON and into HttpResponse object.
- **@RestController** to annotate controller classes.


## Usage
### RestControllers and Mappings
Define your API endpoints with controller classes using `@RestController` and `@RequestMapping` annotations. `@RestController` annotates the `ProductController` class as a controller so that it can receive HTPP requests. The `@RequestMapping` annotation sets the base path for the `ProductController` as `/api/products`.
```
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/{id}")
    @ResponseBody
    public Product getProduct(@PathVariable String id) {
        // Logic to fetch product by id
        return productService.getProductById(id);
    }
}
```
### Authentication
Use the `@Authentication` annotation to secure specific controller methods.

```
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/profile")
    @Authentication // Custom authentication check
    public User getProfile() {
        // Logic to return user profile
    }
}
```
Path Variables
Path variables like /api/products/{id} are automatically extracted and passed as method arguments.

java
Copy code
@GetMapping("/{id}")
public Product getProduct(@PathVariable String id) {
    // Fetch product by id
}
Custom Filters
Add filters to handle request pre-processing, such as validating authentication tokens.

java
Copy code
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Authentication logic here
        chain.doFilter(request, response); // Continue if authenticated
    }
}
