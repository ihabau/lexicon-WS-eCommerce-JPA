package se.lexicon.lexiconwsecommercejpa.service;

import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.lexicon.lexiconwsecommercejpa.dto.*;
import se.lexicon.lexiconwsecommercejpa.entity.*;
import se.lexicon.lexiconwsecommercejpa.exception.*;
import se.lexicon.lexiconwsecommercejpa.mapper.*;
import se.lexicon.lexiconwsecommercejpa.repository.*;


@Service
class OrderServiceImpl implements OrderService {

  private OrderRepository orderRepository;
  private CustomerRepository customerRepository;
  private ProductRepository productRepository;
  private OrderMapper orderMapper;

  public OrderServiceImpl(
  OrderRepository orderRepository,
  CustomerRepository customerRepository,
  ProductRepository productRepository,
  OrderMapper orderMapper
      ) {

  this.orderRepository = orderRepository;
  this.customerRepository = customerRepository;
  this.productRepository = productRepository;
  this.orderMapper = orderMapper;
      }


  //  1. customer arrives FULL, not empty - the id was just the address, this
  //     fetches the row. A bad id throws here and the mapper is never reached.
  //  2. productsById is a Map, not a List, so the mapper looks products up by id
  //     and can never grab the wrong one by accident.
  //  3. status = CREATED is set INSIDE the mapper. Do not set it again here.
  //  4. ONE save, not a loop: Order.items is cascade = ALL, so this single call
  //     inserts the order AND every OrderItem. Saving items separately is two
  //     writes where one is meant.
  //  5. Map the SAVED object - save() returns the managed instance with the id
  //     filled in, so mapping the one you built would answer with id = null.
  @Override
  @Transactional
  public OrderResponse placeOrder(OrderRequest request) {
    Customer customer = customerRepository.findById(request.customerId())
      .orElseThrow(() -> new ResourceNotFoundException("Customer not found " + request.customerId()));

      Map<Long, Product> productsById = new HashMap<>();  // product list(map) populated in the for loop
      for (OrderItemRequest item: request.items()) {
          Product product = productRepository.findById(item.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + item.productId()));
          productsById.put(item.productId(), product);
      }

      Order order = orderMapper.toEntity(request, customer, productsById);

      return orderMapper.toResponse(orderRepository.save(order));
    }
  
 
}


/*
 * @Transactional - the one question to be ready to answer: ALL OR NOTHING.
 * If product 3 of 5 does not exist, step 2 throws and nothing was written
 * anyway. But if the save or the response mapping fails AFTER a successful
 * write, the database keeps a real order the client believes failed - the shop
 * has taken the order and there is no record of it. @Transactional rolls the
 * whole thing back.
 *
 * It goes on the method that spans SEVERAL repository calls. ProductService's
 * findAll() does not need it - one query cannot half-finish.
 *
 * It only rolls back on an UNCHECKED exception, which is why
 * ResourceNotFoundException extending RuntimeException is the right base class.
 *
 * The annotation must STAY: toResponse walks three LAZY associations
 * (order.items, order.customer, orderItem.product), and touching a lazy
 * association with no open Hibernate session throws LazyInitializationException.
 * This is what keeps the session open.
 *
 * If you ever add a second @Transactional import to this file, pick
 * org.springframework.transaction.annotation. jakarta.transaction.Transactional
 * looks identical in the import list and behaves differently on checked
 * exceptions. The jakarta one is used elsewhere in this project (on the
 * repository and on Order.items), which is legal and not a reason to copy it.
 */
