package se.lexicon.lexiconwsecommercejpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.lexicon.lexiconwsecommercejpa.dto.CategoryResponse;
import se.lexicon.lexiconwsecommercejpa.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  // TODO: Part 4.
  // NOTE the shape difference from the other three create endpoints: this one
  // takes a bare String, not a request record. A category is a single field, so
  // there is nothing to wrap - the client posts the name itself as the body.
  // Decide deliberately what @RequestBody binds here: a raw String gives you
  // the body verbatim, so `curl -d "Books"` arrives as Books WITH the quotes
  // still attached unless the client sends a JSON string ("Books") instead.
  @PostMapping
  public ResponseEntity<CategoryResponse> create(@RequestBody String name) {
    throw new UnsupportedOperationException("TODO: CategoryController.create");
  }

  // TODO: Part 4.
  @GetMapping
  public ResponseEntity<List<CategoryResponse>> findAll() {
    throw new UnsupportedOperationException("TODO: CategoryController.findAll");
  }
}
