package com.hako.book.book;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hako.book.common.PageResponse;
import com.hako.book.exception.OperationNotPermittedException;
import com.hako.book.file.fileStorageService;
import com.hako.book.history.BookTransactionHistory;
import com.hako.book.history.BookTransactionHistoryRepository;
import com.hako.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final fileStorageService fileStorageService;
  
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

  @Override
  public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);

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

  @Override
  public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBarrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
    List<BorrowedBookResponse> bookResponse = allBarrowedBooks.stream()
      .map(bookMapper::toBorrowedBookResponse)
      .toList();

      return new PageResponse<>(
        bookResponse,
        allBarrowedBooks.getNumber(),
        allBarrowedBooks.getSize(),
        allBarrowedBooks.getTotalElements(),
        allBarrowedBooks.getTotalPages(),
        allBarrowedBooks.isFirst(),
        allBarrowedBooks.isLast()
      );
  }

  @Override
  public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBarrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
    List<BorrowedBookResponse> bookResponse = allBarrowedBooks.stream()
      .map(bookMapper::toBorrowedBookResponse)
      .toList();

      return new PageResponse<>(
        bookResponse,
        allBarrowedBooks.getNumber(),
        allBarrowedBooks.getSize(),
        allBarrowedBooks.getTotalElements(),
        allBarrowedBooks.getTotalPages(),
        allBarrowedBooks.isFirst(),
        allBarrowedBooks.isLast()
      );
  }

  @Override
  public Integer updateShareable(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
      .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));
    User user = (User) connectedUser.getPrincipal();
    if (!Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot update books shareable status");
    }

    book.setShareable(!book.isShareable());
    bookRepository.save(book);
    return bookId;
  }

  @Override
  public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
      .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));
    User user = (User) connectedUser.getPrincipal();
    if (!Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot update books archived status");
    }

    book.setArchived(!book.isArchived());
    bookRepository.save(book);
    return bookId;
  }

  @Override
  public Integer borrowBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
      .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));

    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("You cannot barrow this book");
    }
      
    User user = (User) connectedUser.getPrincipal();
    if (Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot barrow your own book");
    }

    final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
    if(isAlreadyBorrowed){
      throw new OperationNotPermittedException("You already borrowed this book");
    }

    BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
      .book(book)
      .user(user)
      .returned(false)
      .returnApproved(false)
      .build();

    return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
  }

  @Override
  public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
    .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));

    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("You cannot barrow this book");
    }
      
    User user = (User) connectedUser.getPrincipal();    
    if (Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot barrow your own book");
    }

    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
      .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

    bookTransactionHistory.setReturned(true);
    bookTransactionHistoryRepository.save(bookTransactionHistory);

    return bookId;
  }

  @Override
  public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
    .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));

    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("You cannot barrow this book");
    }
      
    User user = (User) connectedUser.getPrincipal();
    if (Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot barrow your own book");
    }

    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
      .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve the return"));

    bookTransactionHistory.setReturnApproved(true);
    bookTransactionHistoryRepository.save(bookTransactionHistory);
    return bookId;
  }

  @Override
  public void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
    .orElseThrow(() -> new EntityNotFoundException("No book found with id " + bookId));    

    User user = (User) connectedUser.getPrincipal();

    String bookCover = fileStorageService.saveFile(file, user.getId());
    book.setBookCover(bookCover);
    bookRepository.save(book);
  }
}
