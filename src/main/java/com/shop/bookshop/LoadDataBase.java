package com.shop.bookshop;

import com.shop.bookshop.etities.Book;
import com.shop.bookshop.etities.Purchase;
import com.shop.bookshop.etities.Status;
import com.shop.bookshop.repositories.BookRepository;
import com.shop.bookshop.repositories.PurchaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
class LoadDataBase {

    private static final Logger log = LoggerFactory.getLogger(LoadDataBase.class);

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepository, PurchaseRepository purchaseRepository) {
        Collection<String[]> books = new ArrayList<>();
        books.add(new String[]{"Королева Марго",                "Дюма Александр",   "1995", "289"});
        books.add(new String[]{"Три мушкетера",                 "Дюма Александр",   "1987", "456"});
        books.add(new String[]{"Приключения Шерлока Холмса",    "Конан Дойл Артур", "1994", "754"});
        books.add(new String[]{"Граф Монте-Кристо",             "Дюма Александр",   "1992", "457"});
        books.add(new String[]{"Голова профессора Доуэля",      "Беляев Александр", "2017", "835"});
        books.add(new String[]{"Человек-амфибия",               "Беляев Александр", "2015", "236"});

        for (String[] book: books) {
            Book newBook = new Book();
            newBook.setName(book[0]);
            newBook.setAuthor(book[1]);
            newBook.setYear(Integer.parseInt(book[2]));
            newBook.setPrice(Double.parseDouble(book[3]));
            bookRepository.save(newBook);
        }

        Purchase purchase1 = new Purchase();
        purchase1.setNameOfBook("Три мушкетера");
        purchase1.setBookID(2L);
        purchase1.setStatus(Status.CANCELLED);
        purchaseRepository.save(purchase1);

        Purchase purchase2 = new Purchase();
        purchase2.setNameOfBook("Голова профессора Доуэля");
        purchase2.setBookID(5L);
        purchase2.setStatus(Status.IN_PROGRESS);
        purchaseRepository.save(purchase2);

        return args -> {
            log.info("Database loaded");
        };
    }
}
