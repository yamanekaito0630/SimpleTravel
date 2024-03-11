package com.example.simpletravel.repository;

import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.Review;
import com.example.simpletravel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    public Page<Review> findByHouseOrderByUpdatedAtDesc(House house, Pageable pageable);
    public Review findFirstByHouseAndUser(House house, User user);
    public List<Review> findTop4ByHouseOrderByUpdatedAtDesc(House house);
    public List<Review> findByHouseAndUser(House house, User user);
}
