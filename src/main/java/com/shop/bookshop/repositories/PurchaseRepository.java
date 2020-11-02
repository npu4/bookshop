package com.shop.bookshop.repositories;

import com.shop.bookshop.etities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
