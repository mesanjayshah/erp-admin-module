package com.mesanjay.admin.repository;

import com.mesanjay.admin.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query(value = "update cart_items set shopping_cart_id = null where shopping_cart_id = ?1", nativeQuery = true)
    void deleteCartItemById(Long cartId);
}
