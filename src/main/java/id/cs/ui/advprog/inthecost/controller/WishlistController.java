package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    // Constants for repeated string literals
    private static final String STATUS_KEY = "status";
    private static final String MESSAGE_KEY = "message";
    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";
    private static final String INVALID_ID_FORMAT_MESSAGE = "Invalid ID format: ";

    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Kost>> getWishlistByUserId(@PathVariable String userId) {
        log.info("Fetching wishlist for user: {}", userId);
        
        try {
            UUID userUUID = UUID.fromString(userId);
            log.debug("Converted userId to UUID: {}", userUUID);
            
            long startTime = System.currentTimeMillis();
            List<Kost> list = wishlistService.getWishlistByUserId(userUUID);
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("Successfully retrieved {} items from wishlist for user: {} in {}ms", 
                    list.size(), userId, duration);
            
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            log.error("Invalid userId format: {} - {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("Null pointer exception for userId: {} - {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error retrieving wishlist for user: {} - {}", userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving wishlist: " + e.getMessage(), e);
        }
    }    @PostMapping("/{userId}/add/{kostId}")
    public ResponseEntity<Map<String, String>> addToWishlist(@PathVariable String userId, @PathVariable String kostId) {
        log.info("Adding kost {} to wishlist for user: {}", kostId, userId);
        
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);
            log.debug("Converted IDs - userId: {}, kostId: {}", userUUID, kostUUID);

            long startTime = System.currentTimeMillis();
            wishlistService.addToWishlist(userUUID, kostUUID);
            long duration = System.currentTimeMillis() - startTime;

            log.info("Successfully added kost {} to wishlist for user: {} in {}ms", kostId, userId, duration);

            Map<String, String> response = new HashMap<>();
            response.put(STATUS_KEY, SUCCESS_STATUS);
            response.put(MESSAGE_KEY, "Kost added to wishlist successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameter format for add wishlist - userId: {}, kostId: {} - {}", userId, kostId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error adding kost {} to wishlist for user: {} - {}", kostId, userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding to wishlist: " + e.getMessage(), e);
        }
    }    @DeleteMapping("/{userId}/remove/{kostId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable String userId, @PathVariable String kostId) {
        log.info("Removing kost {} from wishlist for user: {}", kostId, userId);
        
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);
            log.debug("Converted IDs - userId: {}, kostId: {}", userUUID, kostUUID);

            long startTime = System.currentTimeMillis();
            wishlistService.removeFromWishlist(userUUID, kostUUID);
            long duration = System.currentTimeMillis() - startTime;

            log.info("Successfully removed kost {} from wishlist for user: {} in {}ms", kostId, userId, duration);

            Map<String, String> response = new HashMap<>();
            response.put(STATUS_KEY, SUCCESS_STATUS);
            response.put(MESSAGE_KEY, "Kost removed from wishlist successfully");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameter format for remove wishlist - userId: {}, kostId: {} - {}", userId, kostId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error removing kost {} from wishlist for user: {} - {}", kostId, userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing from wishlist: " + e.getMessage(), e);
        }
    }    @GetMapping("/{userId}/check/{kostId}")
    public ResponseEntity<Map<String, Boolean>> isInWishlist(@PathVariable String userId, @PathVariable String kostId) {
        log.debug("Checking if kost {} is in wishlist for user: {}", kostId, userId);
        
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);

            long startTime = System.currentTimeMillis();
            boolean isInWishlist = wishlistService.isInWishlist(userUUID, kostUUID);
            long duration = System.currentTimeMillis() - startTime;

            log.debug("Wishlist check result for user: {}, kost: {} -> {} ({}ms)", 
                     userId, kostId, isInWishlist, duration);

            Map<String, Boolean> response = new HashMap<>();
            response.put("inWishlist", isInWishlist);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameter format for wishlist check - userId: {}, kostId: {} - {}", userId, kostId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error checking wishlist for user: {}, kost: {} - {}", userId, kostId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error checking wishlist: " + e.getMessage(), e);
        }
    }    @GetMapping("/count/{kostId}")
    public ResponseEntity<Map<String, Integer>> getWishlistCount(@PathVariable String kostId) {
        log.debug("Getting wishlist count for kost: {}", kostId);
        
        try {
            UUID kostUUID = UUID.fromString(kostId);

            long startTime = System.currentTimeMillis();
            int count = wishlistService.countWishlistsByKostId(kostUUID);
            long duration = System.currentTimeMillis() - startTime;

            log.info("Wishlist count for kost: {} -> {} users ({}ms)", kostId, count, duration);

            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid kostId format for count: {} - {}", kostId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error getting wishlist count for kost: {} - {}", kostId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error counting wishlists: " + e.getMessage(), e);
        }
    }    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException handled in WishlistController: {}", e.getMessage());
        
        Map<String, String> response = new HashMap<>();
        response.put(STATUS_KEY, ERROR_STATUS);
        response.put(MESSAGE_KEY, INVALID_ID_FORMAT_MESSAGE + e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}