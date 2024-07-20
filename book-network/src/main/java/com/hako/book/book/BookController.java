package com.hako.book.book;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hako.book.common.PageResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;



@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "The book API")
public class BookController {
  private final BookService bookService;

  @PostMapping()
  public ResponseEntity<Integer> saveBook(@RequestBody @Valid BookRequest request, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.save(request, connectedUser));
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> findBookById(@PathVariable Integer bookId) {
      return ResponseEntity.ok(bookService.findById(bookId));
  }
  
  @GetMapping()
  public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
    @RequestParam(name = "page", defaultValue = "1", required = false) int page,
    @RequestParam(name = "size", defaultValue = "20", required = false) int size,
    Authentication connectedUser
  ){
    return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
  }

  @GetMapping("/owner")
  public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
    @RequestParam(name = "page", defaultValue = "1", required = false) int page,
    @RequestParam(name = "size", defaultValue = "20", required = false) int size,
    Authentication connectedUser
  ) {
    return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
  }

  @GetMapping("/borrowed")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
    @RequestParam(name = "page", defaultValue = "1", required = false) int page,
    @RequestParam(name = "size", defaultValue = "20", required = false) int size,
    Authentication connectedUser
  ) {
    return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
  }

  @GetMapping("/returned")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
    @RequestParam(name = "page", defaultValue = "1", required = false) int page,
    @RequestParam(name = "size", defaultValue = "20", required = false) int size,
    Authentication connectedUser
  ) {
    return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
  }

  @PatchMapping("/shareable/{bookId}")
  public ResponseEntity<Integer> updateShareableStatus(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.updateShareable(bookId, connectedUser));
  }

  @PatchMapping("/archived/{bookId}")
  public ResponseEntity<Integer> updateArchivedStatus(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
  }

  @PostMapping("/borrow/{bookId}")
  public ResponseEntity<Integer> borrowBook(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
  }
  
  @PatchMapping("/barrow/return/{bookId}")
  public ResponseEntity<Integer> returnBorrowedBook(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, connectedUser));
  }

  @PatchMapping("/barrow/return/approve/{bookId}")
  public ResponseEntity<Integer> approveReturnBorrowedBook(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId, connectedUser));
  }
  
  @PostMapping(value = "/cover/{bookId}", consumes = "multipart/form-data")
  public ResponseEntity<?> uploadBookCoverPicture(@PathVariable("bookId") Integer bookId, @Parameter() @RequestPart("file") MultipartFile file, Authentication connectedUser) {
    return ResponseEntity.ok(bookService.uploadBookCoverPicture(bookId, file, connectedUser));
  }
  
}
