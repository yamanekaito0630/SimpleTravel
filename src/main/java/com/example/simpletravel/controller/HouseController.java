package com.example.simpletravel.controller;

import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.Review;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.form.ReservationInputForm;
import com.example.simpletravel.repository.HouseRepository;
import com.example.simpletravel.repository.ReviewRepository;
import com.example.simpletravel.security.UserDetailsImpl;
import com.example.simpletravel.service.FavoriteService;
import com.example.simpletravel.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    public HouseController(HouseRepository houseRepository,
                           ReviewRepository reviewRepository,
                           ReviewService reviewService,
                           FavoriteService favoriteService) {
        this.houseRepository = houseRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                        Model model) {
        Page<House> housePage;
        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = this.houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = this.houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else if (area != null && !area.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = this.houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = this.houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }
        } else if (price != null) {
            if (order != null && order.equals("priceAsc")) {
                housePage = this.houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = this.houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }
        } else {
            if (order != null && order.equals("priceAsc")) {
                housePage = this.houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = this.houseRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);
        model.addAttribute("order", order);

        return "houses/index";
    }

    @GetMapping("/{id}")
    public String show(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable(name = "id") Integer id, Model model) {
        House house = this.houseRepository.getReferenceById(id);
        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            model.addAttribute("user", user);
            model.addAttribute("favoriteIsNull", this.favoriteService.isNull(house, user));
            model.addAttribute("isPosted", this.reviewService.isPosted(house, user));

        }
        List<Review> reviews = this.reviewRepository.findTop4ByHouseOrderByUpdatedAtDesc(house);
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reviews", reviews);
        return "houses/show";
    }
}
