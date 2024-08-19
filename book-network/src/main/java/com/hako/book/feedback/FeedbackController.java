package com.hako.book.feedback;

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
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {
  
  private final FeedbackService feedbackService;

  @PostMapping("")
  public ResponseEntity<Integer> saveFeedback(@RequestBody @Valid FeedbackRequest request, Authentication connectedUser) {
    return ResponseEntity.ok(feedbackService.save(request, connectedUser));
  }

  @GetMapping("/book/{bookId}")
  public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
    @PathVariable Integer bookId,
    @RequestParam(defaultValue = "0", required = false) Integer page,
    @RequestParam(defaultValue = "10", required = false) Integer size,
    Authentication connectedUser
  ) {
    return ResponseEntity.ok(feedbackService.findAllFeedbackByBook(bookId, page, size, connectedUser));
  }
  
  

}
