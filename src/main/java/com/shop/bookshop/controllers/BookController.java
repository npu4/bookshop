package com.shop.bookshop.controllers;

import com.shop.bookshop.assemblers.BookModelAssembler;
import com.shop.bookshop.etities.Book;
import com.shop.bookshop.exceptions.BookNotFoundException;
import com.shop.bookshop.repositories.BookRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final BookModelAssembler bookModelAssembler;

    public BookController(BookRepository bookRepository, BookModelAssembler bookModelAssembler) {
        this.bookRepository = bookRepository;
        this.bookModelAssembler = bookModelAssembler;
    }

    @GetMapping("/books")
    public CollectionModel<EntityModel<Book>> allBooks() {
        List<EntityModel<Book>> books = bookRepository.findAll().stream()
                .map(bookModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(books,
                linkTo(methodOn(BookController.class).allBooks()).withSelfRel());
    }

    @PostMapping("/books")
    Book newBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping("/books/{id}")
    public EntityModel<Book> bookById(@PathVariable Long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookModelAssembler.toModel(book);
    }

    @PutMapping("/books/{id}")
    Book replaceBook(@RequestBody Book newBook, @PathVariable Long id){
        return bookRepository.findById(id)
                .map(book -> {
                    book.setName(newBook.getName());
                    book.setPrice(newBook.getPrice());
                    book.setYear(newBook.getYear());
                    book.setAuthor(newBook.getAuthor());
                    return bookRepository.save(book);
                })
                .orElseGet(() ->{
                    newBook.setId(id);
                    return bookRepository.save(newBook);
                });
    }

    @DeleteMapping("/books/{id}")
    void deleteBook(@PathVariable Long id){
        bookRepository.deleteById(id);
    }
}
