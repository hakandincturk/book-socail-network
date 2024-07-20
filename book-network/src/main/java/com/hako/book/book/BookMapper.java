package com.hako.book.book;

import java.util.function.Function;

import com.hako.book.history.BookTransactionHistory;

public class BookMapper {

  public BookResponse toBookResponse(Book book){
    return Book.builder()
      .id(book.getId())
      .title(book.getTitle())
      .authorName(book.getAuthorName())
      .isbn(book.getIsbn())
      .synopsis(book.getSynopsis())
      .rate(book.getRate())
      .archived(book.isArchived())
      .shareable(book.isShareable())
      .owner(book.getOwner().getFullName())
      .build();
  };

  public Book toBook(BookRequest request) {
    return Book.builder()
      .id(request.id())
      .title(request.title())
      .authorName(request.authorName())
      .isbn(request.isbn())
      .synopsis(request.synopsis())
      .archived(false)
      .shareable(request.shareable())
      .build();
  }

  public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransactionHistory) {
    return BorrowedBookResponse.builder()
      .id(bookTransactionHistory.getBook().getId())
      .title(bookTransactionHistory.getBook().getTitle())
      .authorName(bookTransactionHistory.getBook().getAuthorName())
      .isbn(bookTransactionHistory.getBook().getIsbn())
      .rate(bookTransactionHistory.getBook().getRate())
      .returned(bookTransactionHistory.isReturned())
      .returnApproved(bookTransactionHistory.isReturnApproved())
      .build();
  }
}
