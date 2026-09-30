package se.lexicon.lexiconwsecommercejpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.lexiconwsecommercejpa.dto.CustomerRequest;
import se.lexicon.lexiconwsecommercejpa.dto.CustomerResponse;
import se.lexicon.lexiconwsecommercejpa.exception.EmailAlreadyExistsException;
import se.lexicon.lexiconwsecommercejpa.exception.ResourceNotFoundException;
import se.lexicon.lexiconwsecommercejpa.service.CustomerService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {


} 



