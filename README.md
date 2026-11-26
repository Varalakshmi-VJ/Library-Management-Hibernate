# Library Management System (Spring Boot + Hibernate)

A web application to manage books, members and book issue/return with late-fine calculation.
Built to demonstrate **Hibernate / JPA** features: entity relationships, lazy loading, JPQL,
N+1 problem solutions, transactions and optimistic locking.

## Features
- Add / edit / delete books with multiple authors, search (title, ISBN, category, author) and pagination
- Member registration with validation and duplicate-email check
- Issue and return books: max 3 active books per member, 14-day loan, Rs. 5/day late fine
- Stock is updated automatically when a book is issued or returned

## Hibernate concepts used
| Concept | Where |
|---------|-------|
| `@ManyToMany` (Book <-> Author, join table `book_authors`) | `Book`, `Author` |
| `@OneToMany` / `@ManyToOne` (Member -> Issue <- Book) | `Member`, `Issue` |
| Lazy loading (`FetchType.LAZY`) | all associations |
| **N+1 problem fixed with `JOIN FETCH`** | `MemberRepository.findAllWithIssues`, `IssueRepository.findAllWithDetails` |
| `@BatchSize` / `default_batch_fetch_size` (batch lazy loading) | `Book.authors` |
| JPQL queries with joins, `distinct`, pagination | `BookRepository.search` |
| `@Transactional` (issue = stock update + new record, atomic) | `IssueService` |
| **Optimistic locking (`@Version`)** - prevents issuing the last copy twice | `Book`, `IssueController` |
| Dirty checking (no explicit `save` needed for managed entities) | `IssueService` |
| Bean Validation | `Member`, `BookController.BookForm` |

Hibernate prints every SQL query in the console (`show-sql=true`), so you can see the effect of `JOIN FETCH`.

## Tech stack
Java 17, Spring Boot 3, Spring MVC, Spring Data JPA (Hibernate 6), MySQL, Thymeleaf, Bootstrap 5, Maven

## Run locally
1. Install JDK 17 and MySQL; set your MySQL password in `src/main/resources/application.properties`
   (or env variables `DB_USERNAME` / `DB_PASSWORD`).
2. Run `LibraryApplication` (Eclipse: Run As > Java Application, or `mvn spring-boot:run`).
3. Open http://localhost:8082 - the `library_db` database and sample data are created automatically.

## Screenshots
_Add screenshots here._
