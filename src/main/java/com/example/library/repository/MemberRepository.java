package com.example.library.repository;

import com.example.library.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);

    // Solves the N+1 problem: members AND their issues come in ONE SQL query (JOIN FETCH).
    // Without it, listing 10 members would run 1 query + 10 more (one per member's issues).
    @Query("select distinct m from Member m left join fetch m.issues order by m.name")
    List<Member> findAllWithIssues();
}
