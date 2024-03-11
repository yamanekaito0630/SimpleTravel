package com.example.simpletravel.repository;

import com.example.simpletravel.entity.Favorite;
import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    public Favorite findFirstByHouseAndUser(House house, User user);
    public Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
