package com.hako.book.history;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hako.book.book.Book;
import com.hako.book.user.User;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

  @Query("""
    SELECT history
    FROM BookTransactionHistory history
    WHERE history.user.id = :userId
  """)
  Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

  @Query("""
    SELECT history
    FROM BookTransactionHistory history
    WHERE history.book.owner.id = :userId
  """)
  Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer id);

  @Query("""
    SELECT history
    (COUNT(*) > 0) AS isBarrowed
    FROM BookTransactionHistory history
    WHERE history.user.id = :userId
    AND history.book.id = :bookId
    AND history.returnApproved = false
  """)
  boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

  @Query("""
    SELECT history
    FROM BookTransactionHistory history
    WHERE history.user.id = :userId
    AND history.book.id = :bookId
    AND history.returned = false
    AND history.returnApproved = false
  """)
  Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

  @Query("""
    SELECT history
    FROM BookTransactionHistory history
    WHERE history.book.owner.id = :userId
    AND history.book.id = :bookId
    AND history.returned = true
    AND history.returnApproved = false
  """)
  Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer book, Integer userId);
}
