package com.rc.ecommerce.repository;

import com.rc.ecommerce.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
