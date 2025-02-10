package dev.coderbin;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.rest.CORSConfigBuilder;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.muserver.MuServerBuilder.muServer;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        var jacksonJsonProvider = new JacksonJsonProvider();

        var server = muServer()
                .withHttpPort(8080)
                .addHandler(RestHandlerBuilder.restHandler(new ProductResource())
                        .addCustomReader(jacksonJsonProvider)
                        .addCustomWriter(jacksonJsonProvider)
                        .withOpenApiJsonUrl("/openapi.json")
                        .withCORS(CORSConfigBuilder.corsConfig()
                                .withAllowedOrigins("https://petstore.swagger.io")
                                .withAllowedHeaders("content-type"))
                )
                .start();

        log.info("Started at " + server.uri());

    }
}

record Product(UUID id, String name, double price) {}
record NewProduct(String name, double price) {}

@Path("/api/products")
class ProductResource {

    private List<Product> products = new CopyOnWriteArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProducts() {
        return products;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Product createProduct(NewProduct newProduct) {
        var product = new Product(UUID.randomUUID(), newProduct.name(), newProduct.price());
        products.add(product);
        return product;
    }


}
