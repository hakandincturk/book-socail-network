package com.hako.book.book;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.hako.book.common.PageResponse;

public interface BookService {
  Integer save(BookRequest request, Authentication connectedUser);
  BookResponse findById(Integer bookId);
  PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser);
  PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser);
  PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser);
  PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser);
  Integer updateShareable(Integer bookId, Authentication connectedUser);
  Integer updateArchivedStatus(Integer bookId, Authentication connectedUser);
  Integer borrowBook(Integer bookId, Authentication connectedUser);
  Integer returnBorrowedBook(Integer bookId, Authentication connectedUser);
  Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser);
  void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication connectedUser);
}
