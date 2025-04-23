package com.example.email_scheduler.controller;

import com.example.email_scheduler.payload.EmailRequest;
import com.example.email_scheduler.payload.EmailResponse;
import com.example.email_scheduler.quartz.EmailJob;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class EmailSchedulerController {
    @Autowired
    private Scheduler scheduler;

    @PostMapping("/scheduleEmail")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            //ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimezone());
            if (emailRequest.getTimezone() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new EmailResponse(false, "Timezone cannot be null."));
            }

            ZoneId zoneId;
            try {
                zoneId = ZoneId.of(String.valueOf(emailRequest.getTimezone()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new EmailResponse(false, "Invalid timezone provided."));
            }

            ZonedDateTime dateTime = emailRequest.getDateTime().atZone(zoneId);

            if (dateTime.isBefore(ZonedDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new EmailResponse(false, "Scheduled time must be in the future."));
            }

            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            EmailResponse emailResponse = new EmailResponse(true,jobDetail.getKey().getName(),jobDetail.getKey().getGroup(),"Email scheduled successfully");

            return ResponseEntity.ok(emailResponse);
        } catch (SchedulerException se) {
            log.error("Error scheduling email", se);
            EmailResponse emailResponse = new EmailResponse(false,"error while scheduling email, please try again later");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(emailResponse);
        }
    }
    @GetMapping("/get")
    public ResponseEntity<String> getApiTest(){
        return ResponseEntity.ok("Get API Test - pass");
    }


    private JobDetail buildJobDetail(EmailRequest request) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", request.getEmail());
        jobDataMap.put("subject", request.getSubject());
        jobDataMap.put("body", request.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("send Email trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
