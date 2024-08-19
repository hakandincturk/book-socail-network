package com.hako.book.feedback;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hako.book.book.Book;
import com.hako.book.user.User;
import com.hako.book.book.BookRepository;
import com.hako.book.common.PageResponse;
import com.hako.book.exception.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

  private final FeedbackRepository feedBackRepository;
  private final BookRepository bookRepository;
  private final FeedbackMapper feedBackMapper;
  
  @Override
  public Integer save(@Valid FeedbackRequest request, Authentication connectedUser) {
    Book book = bookRepository.findById(request.bookId())
      .orElseThrow(() -> new EntityNotFoundException("No book found with id " + request.bookId()));

    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("You cannot give a feedback to an archived or unshareable book");
    }

    User user = (User) connectedUser.getPrincipal();
    if (Objects.equals(book.getOwner().getId(), user.getId())) {  
      throw new OperationNotPermittedException("You cannot give a feedback to your own book");
    }

    Feedback feedback = feedBackMapper.toFeedback(request);
    return feedBackRepository.save(feedback).getId();
  }

  @Override
  public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser) {
    Pageable pageable = PageRequest.of(page, size);
    User user = (User) connectedUser.getPrincipal();
    Page<Feedback>  feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
    List<FeedbackResponse> feedbackResponses = feedbacks.stream()
      .map( f -> feedBackMapper.toFeedbackResponse(f, user.getId()))
      .toList();

    return new PageResponse<>(
      feedbackResponses,
      feedbacks.getNumber(),
      feedbacks.getSize(),
      feedbacks.getTotalElements(),
      feedbacks.getTotalPages(),
      feedbacks.isFirst(),
      feedbacks.isLast()
    );
  }
  
}
