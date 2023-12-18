package com.mesanjay.admin.cron;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AdminAccountUnlockJob {

    private final AdminService adminService;

    @Scheduled(cron = "0 */5 * * * *")   //Every five minutes after it's going to execute
    public void unlockUserAccount() {
        List<Admin> lockedUsers = adminService.getExpiredLockedAdmins();
        for (Admin user : lockedUsers) {
            System.out.println("inside cron operation"+ LocalDateTime.now());
            adminService.unlockAccountTimeExpired(user);
        }
    }
}
