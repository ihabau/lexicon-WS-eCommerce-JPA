package se.lexicon.lexiconwsecommercejpa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import se.lexicon.lexiconwsecommercejpa.dto.OrderRequest;
import se.lexicon.lexiconwsecommercejpa.dto.OrderResponse;
import se.lexicon.lexiconwsecommercejpa.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  // TODO: Part 4.
  // One endpoint only, per the spec. The interesting part is not in this file:
  // placeOrder in OrderServiceImpl is @Transactional, so a failure part way
  // through leaves no half-written order behind. The controller reports the
  // outcome; it must not open a transaction of its own or start splitting the
  // work across layers, because then a rollback here would not cover what the
  // service already did.
  @PostMapping
  public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid OrderRequest request) {
    throw new UnsupportedOperationException("TODO: OrderController.placeOrder");
  }
}
