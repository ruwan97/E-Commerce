package com.rc.ecommerce.web.jobs;

import com.rc.ecommerce.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupJob {
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "${token.cleanup.job.cron}")
    public void cleanUpExpiredTokens() {
        tokenRepository.deleteAllExpiredTokens();
    }
}
