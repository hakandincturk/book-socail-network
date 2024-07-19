package com.hako.book.book;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hako.book.common.PageResponse;
import com.hako.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  
  @Override
  public Integer save(BookRequest request, Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();
    Book book = bookMapper.toBook(request);
    book.setOwner(user);
    return bookRepository.save(book).getId();
  }

  @Override
  public BookResponse findById(Integer bookId) {
    return bookRepository.findById(bookId)
      .map(bookMapper::toBookResponse)
      .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));
  }

  @Override
  public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAllDisplayedBooks(pageable, user.getId());
    List<BookResponse> bookResponses = books.stream()
      .map(bookMapper::toBookResponse)
      .toList();
      
    return new PageResponse<>(
      bookResponses,
      books.getNumber(),
      books.getSize(),
      books.getTotalElements(),
      books.getTotalPages(),
      books.isFirst(),
      books.isLast()
    );
  }
  
  
}
