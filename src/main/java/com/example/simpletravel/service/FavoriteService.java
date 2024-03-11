package com.example.simpletravel.service;

import com.example.simpletravel.entity.Favorite;
import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.repository.FavoriteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository){
        this.favoriteRepository=favoriteRepository;
    }

    @Transactional
    public void create(House house, User user){
        Favorite favorite = new Favorite();
        favorite.setHouse(house);
        favorite.setUser(user);
        this.favoriteRepository.save(favorite);
    }

    public boolean isNull(House house, User user){
        return this.favoriteRepository.findFirstByHouseAndUser(house, user) == null;
    }
}
