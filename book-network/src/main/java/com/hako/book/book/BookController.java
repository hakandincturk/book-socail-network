package com.hako.book.book;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hako.book.common.PageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



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

  
  
}
