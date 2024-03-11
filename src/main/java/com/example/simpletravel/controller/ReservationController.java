package com.example.simpletravel.controller;

import com.example.simpletravel.entity.House;
import com.example.simpletravel.entity.Reservation;
import com.example.simpletravel.entity.Review;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.form.ReservationInputForm;
import com.example.simpletravel.form.ReservationRegisterForm;
import com.example.simpletravel.repository.HouseRepository;
import com.example.simpletravel.repository.ReservationRepository;
import com.example.simpletravel.repository.ReviewRepository;
import com.example.simpletravel.security.UserDetailsImpl;
import com.example.simpletravel.service.ReservationService;
import com.example.simpletravel.service.ReviewService;
import com.example.simpletravel.service.StripeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationService reservationService;
    private final ReviewService reviewService;
    private final StripeService stripeService;

    public ReservationController(ReservationRepository reservationRepository,
                                 HouseRepository houseRepository,
                                 ReviewRepository reviewRepository,
                                 ReservationService reservationService,
                                 ReviewService reviewService,
                                 StripeService stripeService) {
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.reviewRepository = reviewRepository;
        this.reservationService = reservationService;
        this.reviewService = reviewService;
        this.stripeService = stripeService;
    }

    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                        Model model) {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = this.reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        model.addAttribute("reservationPage", reservationPage);
        return "reservations/index";
    }

    @GetMapping("/houses/{id}/reservations/input")
    public String input(@PathVariable(name = "id") Integer id,
                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @ModelAttribute @Validated ReservationInputForm reservationInputForm,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        House house = this.houseRepository.getReferenceById(id);
        Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
        Integer capacity = house.getCapacity();
        if (numberOfPeople != null) {
            if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "宿泊人数が定員を超えています。");
                bindingResult.addError(fieldError);
            }
        }
        if (bindingResult.hasErrors()) {
            User user = userDetailsImpl.getUser();
            List<Review> reviews = this.reviewRepository.findTop4ByHouseOrderByUpdatedAtDesc(house);
            model.addAttribute("house", house);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            model.addAttribute("isPosted", this.reviewService.isPosted(house, user));
            model.addAttribute("reviews", reviews);
            return "houses/show";
        }
        redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
        return "redirect:/houses/{id}/reservations/confirm";
    }

    @GetMapping("/houses/{id}/reservations/confirm")
    public String confirm(@PathVariable(name = "id") Integer id,
                          @ModelAttribute ReservationInputForm reservationInputForm,
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                          HttpServletRequest httpServletRequest,
                          Model model) {
        House house = this.houseRepository.getReferenceById(id);
        User user = userDetailsImpl.getUser();

        // チェックイン日とチェックアウト日を取得
        LocalDate checkinDate = reservationInputForm.getCheckinDate();
        LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
        // 宿泊料金を計算
        Integer price = house.getPrice();
        Integer amount = this.reservationService.calculateAmount(checkinDate, checkoutDate, price);

        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(
                house.getId(),
                user.getId(),
                checkinDate.toString(),
                checkoutDate.toString(),
                reservationInputForm.getNumberOfPeople(),
                amount
        );
        String sessionId = this.stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);
        model.addAttribute("house", house);
        model.addAttribute("reservationRegisterForm", reservationRegisterForm);
        model.addAttribute("sessionId", sessionId);
        return "reservations/confirm";
    }
}
