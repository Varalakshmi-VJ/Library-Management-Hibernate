package com.example.library.repository;

import com.example.library.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    // JOIN FETCH book and member so the list page needs a single SQL query
    @Query(value = "select i from Issue i join fetch i.book join fetch i.member order by i.id desc",
           countQuery = "select count(i) from Issue i")
    Page<Issue> findAllWithDetails(Pageable pageable);

    long countByMemberIdAndReturnDateIsNull(Long memberId);

    boolean existsByBookId(Long bookId);
}
