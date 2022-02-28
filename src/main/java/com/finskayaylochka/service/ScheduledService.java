package com.finskayaylochka.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduledService {

    SendFilesService sendFilesService;

//    @Scheduled(cron = "* */1 * * * ?")
    @SchedulerLock(name = "ScheduledService_sendFiles",// уникальное имя задачи
        lockAtLeastFor = "PT59S", // запускать не чаще, чем раз в 59 сек
        lockAtMostFor = "PT59S") // если нода "умерла" и не отпустила локу, то держит её не более 59 сек
    public void sendFiles() {
        sendFilesService.sendFiles();
    }
}
