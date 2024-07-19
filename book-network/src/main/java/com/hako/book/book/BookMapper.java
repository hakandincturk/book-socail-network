package com.hako.book.book;

import java.util.function.Function;

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
}
