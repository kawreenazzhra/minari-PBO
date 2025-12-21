package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.CartItem;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find cart item by shopping cart and product
    Optional<CartItem> findByShoppingCartAndProduct(ShoppingCart shoppingCart, Product product);
    
    // Find all cart items for a specific shopping cart
    List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
    
    // Find cart items by shopping cart ID
    List<CartItem> findByShoppingCartId(Long cartId);
    
    // Find cart items by product ID
    List<CartItem> findByProductId(Long productId);
    
    // Count items in a shopping cart
    long countByShoppingCart(ShoppingCart shoppingCart);
    
    // Count items in a shopping cart by cart ID
    long countByShoppingCartId(Long cartId);
    
    // Delete all items from a shopping cart
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.shoppingCart.id = :cartId")
    void deleteByShoppingCartId(@Param("cartId") Long cartId);
    
    // Delete specific item from cart by cart ID and product ID
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.shoppingCart.id = :cartId AND ci.product.id = :productId")
    void deleteByShoppingCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    // Find cart item by cart ID and product ID
    @Query("SELECT ci FROM CartItem ci WHERE ci.shoppingCart.id = :cartId AND ci.product.id = :productId")
    Optional<CartItem> findByShoppingCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    // Update quantity of a cart item
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :itemId")
    void updateQuantity(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);
    
    // Update unit price of cart items when product price changes
    @Modifying
    @Query("UPDATE CartItem ci SET ci.unitPrice = :newPrice WHERE ci.product.id = :productId")
    void updateUnitPriceByProductId(@Param("productId") Long productId, @Param("newPrice") Double newPrice);
    
    // Calculate total value of cart items for a specific cart
    @Query("SELECT SUM(ci.quantity * ci.unitPrice) FROM CartItem ci WHERE ci.shoppingCart.id = :cartId")
    Double calculateCartTotal(@Param("cartId") Long cartId);
    
    // Check if product exists in any cart
    boolean existsByProductId(Long productId);
    
    // Check if specific product exists in a specific cart
    boolean existsByShoppingCartIdAndProductId(Long cartId, Long productId);
}