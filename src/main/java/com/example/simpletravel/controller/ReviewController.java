package com.example.simpletravel.controller;

import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.Review;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.form.ReviewEditForm;
import com.example.simpletravel.form.ReviewPostForm;
import com.example.simpletravel.repository.HouseRepository;
import com.example.simpletravel.repository.ReviewRepository;
import com.example.simpletravel.security.UserDetailsImpl;
import com.example.simpletravel.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final HouseRepository houseRepository;
    private final ReviewService reviewService;

    public ReviewController(ReviewRepository reviewRepository, HouseRepository houseRepository, ReviewService reviewService) {
        this.reviewRepository = reviewRepository;
        this.houseRepository = houseRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/houses/{id}/reviews")
    public String index(@PathVariable(name = "id") Integer id,
                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                        Model model) {
        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            model.addAttribute("user", user);
        }

        House house = this.houseRepository.getReferenceById(id);
        Page<Review> reviewPage = this.reviewRepository.findByHouseOrderByUpdatedAtDesc(house, pageable);
        model.addAttribute("house", house);
        model.addAttribute("reviewPage", reviewPage);
        return "reviews/index";
    }

    @GetMapping("/houses/{id}/post")
    public String post(@PathVariable(name = "id") Integer id,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       Model model) {
        House house = this.houseRepository.getReferenceById(id);
        User user = userDetailsImpl.getUser();

        // 投稿済みであればリダイレクト
        if (this.reviewService.isPosted(house, user)) {
            return "redirect:/houses/" + house.getId();
        }
        model.addAttribute("reviewPostForm", new ReviewPostForm());
        model.addAttribute("house", house);
        return "/reviews/post";
    }

    @PostMapping("/houses/{id}/post")
    public String post(@PathVariable(name = "id") Integer houseId,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       @ModelAttribute @Validated ReviewPostForm reviewPostForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        House house = this.houseRepository.getReferenceById(houseId);
        User user = userDetailsImpl.getUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("house", house);
            return "/reviews/post";
        }
        this.reviewService.create(reviewPostForm, house, user);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
        return "redirect:/houses/" + houseId;
    }

    @GetMapping("/houses/{id}/reviews/edit")
    public String edit(@PathVariable(name = "id") Integer houseId,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       Model model) {
        House house = this.houseRepository.getReferenceById(houseId);
        User user = userDetailsImpl.getUser();
        Review review = this.reviewRepository.findFirstByHouseAndUser(house, user);
        model.addAttribute("house", house);
        model.addAttribute("reviewEditForm", new ReviewEditForm(
                review.getNumberOfStars(),
                review.getComment()
        ));
        return "/reviews/edit";
    }

    @PostMapping("/houses/{id}/reviews/update")
    public String update(@PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         @ModelAttribute @Validated ReviewEditForm reviewEditForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        House house = this.houseRepository.getReferenceById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("house", house);
            return "/reviews/edit";
        }
        this.reviewService.update(reviewEditForm, house, userDetailsImpl.getUser());
        redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました。");
        return "redirect:/houses/" + house.getId();
    }

    @GetMapping("/houses/{id}/reviews/delete")
    public String delete(@PathVariable(name = "id") Integer id,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes) {
        Integer reviewId = this.reviewRepository.findFirstByHouseAndUser(
                this.houseRepository.getReferenceById(id),
                userDetailsImpl.getUser()
        ).getId();
        this.reviewRepository.deleteById(reviewId);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
        return "redirect:/houses/" + id;
    }
}
