package com.hako.book.book;

import org.springframework.security.core.Authentication;

import com.hako.book.common.PageResponse;

public interface BookService {
  Integer save(BookRequest request, Authentication connectedUser);
  BookResponse findById(Integer bookId);
  PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser);
}
