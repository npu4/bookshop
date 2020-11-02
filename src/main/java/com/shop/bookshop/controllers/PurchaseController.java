package com.shop.bookshop.controllers;

import com.shop.bookshop.assemblers.PurchaseModelAssembler;
import com.shop.bookshop.etities.Purchase;
import com.shop.bookshop.etities.Status;
import com.shop.bookshop.exceptions.PurchaseNotFoundException;
import com.shop.bookshop.repositories.PurchaseRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.shop.bookshop.etities.Status.valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PurchaseController {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseModelAssembler purchaseModelAssembler;
    private final BookController bookController;

    public PurchaseController(PurchaseRepository purchaseRepository, PurchaseModelAssembler purchaseModelAssembler, BookController bookController) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseModelAssembler = purchaseModelAssembler;
        this.bookController = bookController;
    }

    @GetMapping("/purchases")
    public CollectionModel<EntityModel<Purchase>> allPurchases() {
        List<EntityModel<Purchase>> purchases = purchaseRepository.findAll().stream()
                .map(purchaseModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(purchases,
                linkTo(methodOn(PurchaseController.class).allPurchases()).withSelfRel());
    }

    @GetMapping("/purchases/{id}")
    public EntityModel<Purchase> purchaseById(@PathVariable Long id) {
        Purchase purchase = purchaseRepository
                .findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));
        return purchaseModelAssembler.toModel(purchase);
    }

    @PostMapping("/purchases")
    ResponseEntity<EntityModel<Purchase>> newPurchase(@RequestBody Purchase purchase) {
        purchase.setStatus(Status.IN_PROGRESS);
        Purchase newPurchase = purchaseRepository.save(purchase);
        return ResponseEntity
                .created(linkTo(methodOn(PurchaseController.class).purchaseById(newPurchase.getId())).toUri())
                .body(purchaseModelAssembler.toModel(newPurchase));
    }

    @DeleteMapping("/purchases/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        Purchase purchase = this.purchaseRepository.findById(id) //
                .orElseThrow(() -> new PurchaseNotFoundException(id));

        if (valid(purchase.getStatus(), Status.CANCELLED)) {
            purchase.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(purchaseModelAssembler.toModel(purchaseRepository.save(purchase)));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't cancel an purchase that is in the " + purchase.getStatus() + " status"));
    }

    @PostMapping("/purchases/{id}")
    public ResponseEntity<?> complete(@PathVariable Long id) {

        Purchase purchase = purchaseRepository.findById(id) //
                .orElseThrow(() -> new PurchaseNotFoundException(id));

        if (valid(purchase.getStatus(), Status.COMPLETED)) {
            purchase.setStatus(Status.COMPLETED);
            bookController.deleteBook(purchase.getBookID());
            return ResponseEntity.ok(purchaseModelAssembler.toModel(purchaseRepository.save(purchase)));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't complete an purchase that is in the " + purchase.getStatus() + " status"));
    }

    @PostMapping("/purchases/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id) {
        Purchase purchase = this.purchaseRepository.findById(id).orElseThrow(() -> new PurchaseNotFoundException(id));

        if (valid(purchase.getStatus(), Status.PAID)) {
            purchase.setStatus(Status.PAID);
            return ResponseEntity.ok(purchaseRepository.save(purchase));
        }

        return ResponseEntity //
                .status(HttpStatus.METHOD_NOT_ALLOWED) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Method not allowed") //
                        .withDetail("You can't complete an purchase that is in the " + purchase.getStatus() + " status"));
    }
}
