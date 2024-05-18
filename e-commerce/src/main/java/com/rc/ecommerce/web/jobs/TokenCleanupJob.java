package com.rc.ecommerce.web.jobs;

import com.rc.ecommerce.repository.TokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupJob {
    private final TokenRepository tokenRepository;

    public TokenCleanupJob(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "${token.cleanup.job.cron}")
    public void cleanUpExpiredTokens() {
        tokenRepository.deleteAllExpiredTokens();
    }
}
