package com.thiru.reviewms.review.impl;

import com.thiru.reviewms.review.Review;
import com.thiru.reviewms.review.ReviewRepository;
import com.thiru.reviewms.review.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService{
	
	
	private ReviewRepository reviewRepository;

	
	
	public ReviewServiceImpl(ReviewRepository reviewRepository) {

		this.reviewRepository = reviewRepository;

	}



	@Override
	public List<Review> getAllReviews(Long companyId) {
		List <Review> reviews=reviewRepository.findByCompanyId(companyId);
		return reviews;
	}



	@Override
	public boolean addReview(Long companyId, Review review) {
		

		if(companyId!=null && review!=null) {
			review.setCompanyId(companyId);
			reviewRepository.save(review);
			return true;
		}
		return false;
	}



	@Override
	public Review getReview( Long reviewId) {
	
		return reviewRepository.findById(reviewId).orElse(null);
	}



	@Override
	public boolean updateReview(Long reviewid, Review updatedReview) {

		Review review=reviewRepository.findById(reviewid).orElse(null);
		if(reviewid!=null)
		{
		
			review.setTitle(updatedReview.getTitle());
			review.setDescription(updatedReview.getDescription());
			review.setRating(updatedReview.getRating());
			review.setCompanyId(updatedReview.getCompanyId());
			reviewRepository.save(review);
			//reviewRepository.save(updatedReview);
			return true;
		}
		else
		{
			return false;
		}
		
	}



	@Override
	public boolean deleteReview(Long reviewId) {
		Review review=reviewRepository.findById(reviewId).orElse(null);
		if(review!=null)
		{

			reviewRepository.delete(review);
			return true;
		}
		return false;
		
	}
	

	
	
}
