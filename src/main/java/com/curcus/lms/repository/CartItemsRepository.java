package com.curcus.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Cart;
import com.curcus.lms.model.entity.CartItems;
import com.curcus.lms.model.entity.CartItemsId;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, CartItemsId> {
    @Query("select a from CartItems a join a.cart b join a.course c where b.cartId = :cartId")
    List<Cart> findAllCourseWithCartId(@Param("cartId") Long cartId);

    @Modifying
    @Transactional
    @Query("delete from CartItems a where a.id.cartId = :cartId")
    void deleteCartItemsById(@Param("cartId") Long cartId);

    // CartItems getCartItemByCompositeId(Long cartId, Long CourseId);
    List<CartItems> findAllByCart_CartId(Long cartId);

    Page<CartItems> findAllByCart_CartId(Long cartId, Pageable pageable);

    @Query("select a from CartItems a where a.id.cartId = :cartId and a.id.courseId = :courseId")
    Optional<CartItems> findByIdCardAndIdCourse(@Param("cartId") Long cartId, @Param("courseId") Long courseId);
}
