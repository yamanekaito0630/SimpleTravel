package com.example.simpletravel.service;

import com.example.simpletravel.entity.Role;
import com.example.simpletravel.entity.User;
import com.example.simpletravel.form.SignupForm;
import com.example.simpletravel.form.UserEditForm;
import com.example.simpletravel.repository.RoleRepository;
import com.example.simpletravel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        Role role = this.roleRepository.findByName("ROLE_GENERAL");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);

        return this.userRepository.save(user);
    }

    @Transactional
    public void update(UserEditForm userEditForm){
        User user=this.userRepository.getReferenceById(userEditForm.getId());

        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        this.userRepository.save(user);
    }

    public boolean isEmailRegistered(String email) {
        User user = this.userRepository.findByEmail(email);
        return user != null;
    }

    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        this.userRepository.save(user);
    }

    public boolean isEmailChanged(UserEditForm userEditForm){
        User currentUser=this.userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());
    }
}
