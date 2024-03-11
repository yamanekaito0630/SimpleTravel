package com.example.simpletravel.service;

import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.Review;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.form.ReviewEditForm;
import com.example.simpletravel.form.ReviewPostForm;
import com.example.simpletravel.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public void create(ReviewPostForm reviewPostForm, House house, User user) {
        Review review = new Review();
        review.setHouse(house);
        review.setUser(user);
        review.setNumberOfStars(reviewPostForm.getNumberOfStars());
        review.setComment(reviewPostForm.getComment());
        this.reviewRepository.save(review);
    }

    @Transactional
    public void update(ReviewEditForm reviewEditForm, House house, User user) {
        Review review = this.reviewRepository.findFirstByHouseAndUser(house, user);
        review.setNumberOfStars(reviewEditForm.getNumberOfStars());
        review.setComment(reviewEditForm.getComment());
        this.reviewRepository.save(review);
    }

    public boolean isPosted(House house, User user) {
        return !this.reviewRepository.findByHouseAndUser(house, user).isEmpty();
    }
}
