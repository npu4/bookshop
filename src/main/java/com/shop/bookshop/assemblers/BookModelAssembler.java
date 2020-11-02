package com.shop.bookshop.assemblers;

import com.shop.bookshop.controllers.BookController;
import com.shop.bookshop.etities.Book;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {
    @Override
    public EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book, //
                linkTo(methodOn(BookController.class).bookById(book.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).allBooks()).withRel("books"));
    }
}