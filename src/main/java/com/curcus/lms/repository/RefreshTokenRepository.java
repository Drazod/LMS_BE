package com.curcus.lms.repository;

import com.curcus.lms.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query(value = """
      select t from RefreshToken t inner join User u\s
      on t.user.userId = u.userId\s
      where u.userId = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<RefreshToken> findAllValidRefreshTokenByUser(Long id);

    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByUser_UserId(Long userId);
}
