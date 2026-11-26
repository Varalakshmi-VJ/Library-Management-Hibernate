package com.example.library.repository;

import com.example.library.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAvailableCopiesGreaterThanOrderByTitle(int copies);

    // JPQL: search by title, ISBN, category or author name (join across the many-to-many)
    @Query(value = """
            select distinct b from Book b left join b.authors a
            where lower(b.title) like lower(concat('%', :q, '%'))
               or lower(b.isbn) like lower(concat('%', :q, '%'))
               or lower(b.category) like lower(concat('%', :q, '%'))
               or lower(a.name) like lower(concat('%', :q, '%'))
            """,
            countQuery = """
            select count(distinct b) from Book b left join b.authors a
            where lower(b.title) like lower(concat('%', :q, '%'))
               or lower(b.isbn) like lower(concat('%', :q, '%'))
               or lower(b.category) like lower(concat('%', :q, '%'))
               or lower(a.name) like lower(concat('%', :q, '%'))
            """)
    Page<Book> search(@Param("q") String q, Pageable pageable);
}
