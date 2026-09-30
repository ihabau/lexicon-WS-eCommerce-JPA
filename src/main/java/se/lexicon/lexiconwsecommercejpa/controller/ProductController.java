package se.lexicon.lexiconwsecommercejpa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import se.lexicon.lexiconwsecommercejpa.dto.ProductRequest;
import se.lexicon.lexiconwsecommercejpa.dto.ProductResponse;
import se.lexicon.lexiconwsecommercejpa.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  // Depends on the INTERFACE, never ProductServiceImpl. The controller is
  // written against the contract; swapping the implementation is then a change
  // nobody outside this package has to notice. This is the whole point of the
  // interface/implementation split Part 3 asked for.
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  // TODO: Part 4.
  // @Valid is what actually runs the constraints on ProductRequest - without it
  // the annotations are only documentation and bad input reaches the service.
  // 201 Created, not 200: the request created something. Location is optional;
  // ResponseEntity.status(...) is not.
  @PostMapping
  public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request) {
    throw new UnsupportedOperationException("TODO: ProductController.create");
  }

  // TODO: Part 4.
  @GetMapping
  public ResponseEntity<List<ProductResponse>> findAll() {
    throw new UnsupportedOperationException("TODO: ProductController.findAll");
  }

  // TODO: Part 4.
  // Two annotations doing different jobs: @RequestParam pulls ?name= out of the
  // QUERY STRING and binds it to the argument, @GetMapping's value is part of
  // the PATH. Mixing the two up is the classic beginner bug here - note the
  // spec's endpoint is /search?name=..., not /search/{name}.
  @GetMapping("/search")
  public ResponseEntity<List<ProductResponse>> searchByName(@RequestParam String name) {
    throw new UnsupportedOperationException("TODO: ProductController.searchByName");
  }
}
