package com.hako.book.feedback;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hako.book.book.Book;

import jakarta.validation.Valid;

@Service
public class FeedbackMapper {

  public Feedback toFeedback(@Valid FeedbackRequest request) {
    return Feedback.builder()
      .note(request.note())
      .comment(request.comment())
      .book(
        Book.builder().id(request.bookId()).archived(false).shareable(false).build()
        )
      .build();
  }

  public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
    return FeedbackResponse.builder()
      .note(feedback.getNote())
      .comment(feedback.getComment())
      .ownFeedBack(Objects.equals(feedback.getCreatedBy(), id))
      // .ownFeedBack(feedback.getUser().getId().equals(id))
      .build();
  }

}
