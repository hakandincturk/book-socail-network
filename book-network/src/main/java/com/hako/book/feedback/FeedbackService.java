package com.hako.book.feedback;

import org.springframework.security.core.Authentication;

import com.hako.book.common.PageResponse;

import jakarta.validation.Valid;

public interface FeedbackService {

  Integer save(@Valid FeedbackRequest request, Authentication connectedUser);
  PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser);

}
