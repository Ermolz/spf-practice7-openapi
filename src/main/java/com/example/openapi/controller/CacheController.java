package com.example.openapi.controller;

import com.example.openapi.cache.CustomCacheManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CustomCacheManager cacheManager;

    @Operation(summary = "Clear all caches", description = "Clears all caches in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All caches cleared successfully",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error clearing caches",
                    content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        try {
            cacheManager.clearAllCaches();
            Map<String, String> response = new HashMap<>();
            response.put("message", "All caches cleared successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error clearing caches: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Clear specific cache", description = "Clears a specific cache by its name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cache cleared successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Cache not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error clearing cache",
                    content = @Content)
    })
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        try {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            if (!cacheNames.contains(cacheName)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Cache '" + cacheName + "' not found");
                response.put("status", "not_found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            cacheManager.clearCache(cacheName);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error clearing cache: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Get cache statistics", description = "Returns statistics about all caches in the system.")
    @ApiResponse(responseCode = "200", description = "Cache statistics retrieved successfully",
            content = @Content)
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            Collection<String> cacheNames = cacheManager.getCacheNames();
            
            Map<String, Integer> cacheSizes = new HashMap<>();
            for (String cacheName : cacheNames) {
                var cache = cacheManager.getCache(cacheName);
                if (cache instanceof com.example.openapi.cache.CustomCache) {
                    cacheSizes.put(cacheName, ((com.example.openapi.cache.CustomCache) cache).size());
                } else {
                    cacheSizes.put(cacheName, 0);
                }
            }
            
            stats.put("totalCaches", cacheNames.size());
            stats.put("cacheNames", cacheNames);
            stats.put("cacheSizes", cacheSizes);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error retrieving cache statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

