package com.castor.guloso.app.scheduler;

import com.castor.guloso.order.service.OrderSessionService;
import com.castor.guloso.whatsapp.service.WhatsAppMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Daily automation:
 * - Morning: send lunch requests (default 10:00)
 * - Cutoff: finalize session + mark remaining as NO_REPLY (default 11:30)
 *
 * Times are configurable via application.properties:
 *   castor.scheduler.default-hour / default-minute
 *   castor.scheduler.cutoff-hour / cutoff-minute
 */
@Component
@EnableScheduling
public class CastorScheduler {

    private static final Logger log = LoggerFactory.getLogger(CastorScheduler.class);

    private final WhatsAppMessageService whatsAppMessageService;
    private final OrderSessionService orderSessionService;

    public CastorScheduler(WhatsAppMessageService whatsAppMessageService,
                           OrderSessionService orderSessionService) {
        this.whatsAppMessageService = whatsAppMessageService;
        this.orderSessionService = orderSessionService;
    }

    /**
     * Send daily lunch requests.
     * Cron: second minute hour day-of-month month day-of-week
     * Default: every day at 10:00 (server local time).
     */
    @Scheduled(cron = "${castor.scheduler.send-cron:0 0 10 * * *}")
    public void sendDailyRequests() {
        log.info("Scheduler: starting daily lunch request send");
        try {
            int sent = whatsAppMessageService.sendDailyLunchRequests();
            log.info("Scheduler: sent {} lunch request(s)", sent);
        } catch (Exception e) {
            log.error("Scheduler: failed to send daily requests — {}", e.getMessage(), e);
        }
    }

    /**
     * Finalize the day: mark pending as NO_REPLY and close the session.
     * Default: every day at 11:30.
     */
    @Scheduled(cron = "${castor.scheduler.cutoff-cron:0 30 11 * * *}")
    public void cutoffAndFinalize() {
        log.info("Scheduler: running cutoff / finalize");
        try {
            orderSessionService.finalizeSession();
            log.info("Scheduler: session finalized, kitchen summary is available at /api/orders/summary");
        } catch (Exception e) {
            // Most common case: no open session (weekend / already closed)
            log.info("Scheduler: finalize skipped — {}", e.getMessage());
        }
    }
}
