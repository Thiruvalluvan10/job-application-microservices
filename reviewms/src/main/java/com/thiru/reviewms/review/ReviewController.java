package com.thiru.reviewms.review;

import com.thiru.reviewms.review.messaging.ReviewMessageProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

	private ReviewService reviewService;

	private ReviewMessageProducer reviewMessageProducer;

	public ReviewController(ReviewService reviewService, ReviewMessageProducer reviewMessageProducer) {
		
		this.reviewService = reviewService;
		this.reviewMessageProducer = reviewMessageProducer;
		//System.out.print("Hello");
	}
	
	@GetMapping()
	public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId)
	{
		return new ResponseEntity<>(reviewService.getAllReviews(companyId),HttpStatus.OK);
	}
	@PostMapping()
	public ResponseEntity<String> addReview(@RequestParam Long companyId,@RequestBody Review review)
	{
		boolean isReviewSaved=reviewService.addReview(companyId, review);
		if(isReviewSaved) {

			reviewMessageProducer.sendMessage(review);
			return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
		}
		return new ResponseEntity<>("Company not found",HttpStatus.NOT_FOUND);
	}
	@GetMapping("/{reviewId}")
	public ResponseEntity<Review> getReview(@PathVariable Long reviewId)
	{
		return new ResponseEntity<>(reviewService.getReview(reviewId),HttpStatus.OK);
	}
	@PutMapping("/{reviewId}")
	public ResponseEntity<String> updateReview(@PathVariable Long reviewId,@RequestBody Review review)
	{
		boolean isReview=reviewService.updateReview(reviewId, review);
		if(isReview)
		{
		return new ResponseEntity<>("Updated Successfully",HttpStatus.OK);
		}
		return new ResponseEntity<>("Review not updated",HttpStatus.NOT_FOUND);
	}
	
	@DeleteMapping("/{reviewId}")
	public  ResponseEntity<String> deleteReveiw(@PathVariable Long reviewId)
	{
		boolean isReviewDeleted = reviewService.deleteReview(reviewId);
		
		if(isReviewDeleted)
		{
			return new ResponseEntity<>("Deleted..!",HttpStatus.OK);
		}
		return new ResponseEntity<>("Not deleted",HttpStatus.NOT_FOUND);
			
	}

	@GetMapping("/averageRating")
	public double getAverageReview(@RequestParam Long companyId)
	{
		List<Review> reviewList=reviewService.getAllReviews(companyId);
		return reviewList.stream().mapToDouble(Review::getRating).average().orElse(0.0);
	}

}
