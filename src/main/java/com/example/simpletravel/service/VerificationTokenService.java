package com.example.simpletravel.service;

import com.example.simpletravel.entity.User;
import com.example.simpletravel.entity.VerificationToken;
import com.example.simpletravel.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository){
        this.verificationTokenRepository=verificationTokenRepository;
    }

    @Transactional
    public void create(User user, String token){
        VerificationToken verificationToken=new VerificationToken();

        verificationToken.setUser(user);
        verificationToken.setToken(token);

        this.verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationToken(String token){
        return this.verificationTokenRepository.findByToken(token);
    }
}
