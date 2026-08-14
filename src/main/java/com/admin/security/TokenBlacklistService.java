


















//
//
//package com.admin.security;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class TokenBlacklistService {
//
//    // token -> expiry instant (when the JWT itself would naturally expire)
//    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
//
//    public void blacklist(String token, Instant expiresAt) {
//        blacklistedTokens.put(token, expiresAt);
//    }
//
//    public boolean isBlacklisted(String token) {
//        Instant expiry = blacklistedTokens.get(token);
//        if (expiry == null) {
//            return false;
//        }
//        // If the token has already naturally expired, no need to keep tracking it —
//        // treat it as "not blacklisted" here since the JWT itself is dead anyway.
//        if (expiry.isBefore(Instant.now())) {
//            blacklistedTokens.remove(token);
//            return false;
//        }
//        return true;
//    }
//
//    // Runs every hour; sweeps out any tokens whose natural expiry has passed
//    @Scheduled(fixedRate = 3_600_000)
//    public void evictExpiredTokens() {
//        Instant now = Instant.now();
//        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
//    }
//}
















package com.admin.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens =
            ConcurrentHashMap.newKeySet();

    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}