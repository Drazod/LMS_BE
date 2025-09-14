package com.curcus.lms.service;

import com.curcus.lms.model.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    List<Token> findAllValidTokenByUser(Long id);
    Optional<Token> findByToken(String token);
}
