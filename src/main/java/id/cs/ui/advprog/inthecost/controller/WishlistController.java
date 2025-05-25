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

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
        try {
            UUID userUUID = UUID.fromString(userId);
            List<Kost> list = wishlistService.getWishlistByUserId(userUUID);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving wishlist: " + e.getMessage(), e);
        }
    }

    @PostMapping("/{userId}/add/{kostId}")
    public ResponseEntity<Map<String, String>> addToWishlist(@PathVariable String userId, @PathVariable String kostId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);

            wishlistService.addToWishlist(userUUID, kostUUID);

            Map<String, String> response = new HashMap<>();
            response.put(STATUS_KEY, SUCCESS_STATUS);
            response.put(MESSAGE_KEY, "Kost added to wishlist successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding to wishlist: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{userId}/remove/{kostId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable String userId, @PathVariable String kostId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID kostUUID = UUID.fromString(kostId);

            wishlistService.removeFromWishlist(userUUID, kostUUID);

            Map<String, String> response = new HashMap<>();
            response.put(STATUS_KEY, SUCCESS_STATUS);
            response.put(MESSAGE_KEY, "Kost removed from wishlist successfully");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing from wishlist: " + e.getMessage(), e);
        }
    }

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error checking wishlist: " + e.getMessage(), e);
        }
    }

    @GetMapping("/count/{kostId}")
    public ResponseEntity<Map<String, Integer>> getWishlistCount(@PathVariable String kostId) {
        try {
            UUID kostUUID = UUID.fromString(kostId);

            int count = wishlistService.countWishlistsByKostId(kostUUID);

            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_ID_FORMAT_MESSAGE + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error counting wishlists: " + e.getMessage(), e);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put(STATUS_KEY, ERROR_STATUS);
        response.put(MESSAGE_KEY, INVALID_ID_FORMAT_MESSAGE + e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}