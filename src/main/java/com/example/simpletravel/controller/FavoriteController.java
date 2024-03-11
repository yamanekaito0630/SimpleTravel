package com.example.simpletravel.controller;

import com.example.simpletravel.entity.Favorite;
import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.repository.FavoriteRepository;
import com.example.simpletravel.repository.HouseRepository;
import com.example.simpletravel.security.UserDetailsImpl;
import com.example.simpletravel.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FavoriteController {
    private final FavoriteRepository favoriteRepository;
    private final HouseRepository houseRepository;
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteRepository favoriteRepository, HouseRepository houseRepository, FavoriteService favoriteService) {
        this.favoriteRepository = favoriteRepository;
        this.houseRepository = houseRepository;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/favorites")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                        Model model) {
        User user = userDetailsImpl.getUser();
        Page<Favorite> favoritePage = this.favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        model.addAttribute("favoritePage", favoritePage);
        return "favorites/index";
    }

    @PostMapping("/favorite/add")
    @ResponseBody
    public boolean add(@RequestParam Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        House house = this.houseRepository.getReferenceById(houseId);
        User user = userDetailsImpl.getUser();
        if (this.favoriteService.isNull(house, user)) {
            this.favoriteService.create(house, user);
            return true;
        }
        return false;
    }

    @PostMapping("/favorite/remove")
    @ResponseBody
    public boolean remove(@RequestParam Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        House house = this.houseRepository.getReferenceById(houseId);
        User user = userDetailsImpl.getUser();
        if (!this.favoriteService.isNull(house, user)) {
            Integer favoriteId = this.favoriteRepository.findFirstByHouseAndUser(house, user).getId();
            this.favoriteRepository.deleteById(favoriteId);
            return true;
        }
        return false;
    }
}
