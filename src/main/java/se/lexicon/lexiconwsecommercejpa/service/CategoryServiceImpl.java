package se.lexicon.lexiconwsecommercejpa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.lexicon.lexiconwsecommercejpa.dto.CategoryResponse;
import se.lexicon.lexiconwsecommercejpa.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  // One dependency, constructor-injected and final - same shape as the other
  // three services in this package. Final because a required-constructor
  // dependency cannot be reassigned anyway, and final means it cannot be
  // reassigned by accident later either.
  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  // TODO: Part 3 optional task.
  // Order of operations to work out: check existsByName first (readable error),
  // then build and save. Note Category.products has NO initialiser - do not
  // append to it here, and do not return the entity.
  @Override
  public CategoryResponse create(String name) {
    throw new UnsupportedOperationException("TODO: CategoryServiceImpl.create");
  }

  // TODO: Part 3 optional task.
  // Remember the entity must not escape this layer: map each Category to a
  // CategoryResponse rather than returning Category.
  @Override
  public List<CategoryResponse> findAll() {
    throw new UnsupportedOperationException("TODO: CategoryServiceImpl.findAll");
  }
}
