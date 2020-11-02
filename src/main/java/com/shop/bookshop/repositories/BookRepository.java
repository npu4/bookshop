package com.shop.bookshop.repositories;

import com.shop.bookshop.etities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select b from Book b where b.name=:name")
    Book findByName(String name);
}
