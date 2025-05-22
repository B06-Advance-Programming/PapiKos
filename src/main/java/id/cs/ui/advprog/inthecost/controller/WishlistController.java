package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing user wishlists
 * Provides endpoints for users to add, remove, and view kosts in their wishlist
 * Also provides endpoints for kost owners to see which users have wishlisted their properties
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Get all wishlisted kosts for a specific user
     * 
     * @param userId The ID of the user
     * @return List of wishlisted kosts
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Kost>> getWishlistByUserId(@PathVariable String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            List<Kost> list = wishlistService.getWishlistByUserId(userUUID);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format: " + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving wishlist: " + e.getMessage(), e);
        }
    }
    
    /**
     * Add a kost to user's wishlist
     * 
     * @param userId The ID of the user
     * @param kostId The ID of the kost to add to wishlist
     * @return Success message
     */
    @PostMapping("/{userId}/add/{kostId}")
    public ResponseEntity<Map<String, String>> addToWishlist(@PathVariable String userId, @PathVariable String kostId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);
            
            wishlistService.addToWishlist(userUUID, kostUUID);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Kost added to wishlist successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding to wishlist: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove a kost from user's wishlist
     * 
     * @param userId The ID of the user
     * @param kostId The ID of the kost to remove from wishlist
     * @return Success message
     */
    @DeleteMapping("/{userId}/remove/{kostId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable String userId, @PathVariable String kostId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);
            
            wishlistService.removeFromWishlist(userUUID, kostUUID);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Kost removed from wishlist successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing from wishlist: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if a kost is in a user's wishlist
     * 
     * @param userId The ID of the user
     * @param kostId The ID of the kost to check
     * @return Boolean indicating if kost is in wishlist
     */
    @GetMapping("/{userId}/check/{kostId}")
    public ResponseEntity<Map<String, Boolean>> isInWishlist(@PathVariable String userId, @PathVariable String kostId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);
            
            boolean isInWishlist = wishlistService.isInWishlist(userUUID, kostUUID);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("inWishlist", isInWishlist);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error checking wishlist: " + e.getMessage(), e);
        }
    }
    
    /**
     * Count how many users have wishlisted a kost
     * Useful for kost owners to see popularity
     * 
     * @param kostId The ID of the kost
     * @return Count of users who wishlisted the kost
     */
    @GetMapping("/count/{kostId}")
    public ResponseEntity<Map<String, Integer>> getWishlistCount(@PathVariable String kostId) {
        try {
            UUID kostUUID = UUID.fromString(kostId);
            
            int count = wishlistService.countWishlistsByKostId(kostUUID);
            
            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid kost ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error counting wishlists: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle UUID format exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid ID format: " + e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}