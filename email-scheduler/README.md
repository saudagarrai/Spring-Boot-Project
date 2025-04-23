Description  
This project is a Spring Boot application that enables scheduling of emails using the Quartz Scheduler.
Users can schedule an email to be sent at a specified date and time, supporting different time zones.
The system ensures that emails are dispatched automatically at the scheduled time.

Topics Covered
- Spring Boot Framework
- Quartz Scheduler for Job Scheduling
- JavaMailSender for Sending Emails
- MySQL Database for Job Storage
- Lombok for Reducing Boilerplate Code
- REST APIs for Scheduling Emails
- Time Zone Handling
- Unit Testing with JUnit
- Maven for Dependency Management


POST URL: http://localhost:8081/scheduleEmail



json body


{
"email": "nitiansaudagar@gmail.com",
"subject": "Your Amazon Order Has Been Cancelled",
"body": "<html><body><h2 style='color:#FF9900;'>Order Cancellation Confirmation</h2><p>Dear Customer,</p><p>We wanted to let you know that your order with <b>Order ID: ORDER12345</b> has been successfully cancelled as per your request.</p><p>If you did not request this cancellation, please contact our customer support team immediately.</p><p>We appreciate your trust in <b>Amazon</b> and look forward to serving you again in the future!</p><br><hr><p><b>Amazon Customer Support</b></p><p>Email: <a href='mailto:support@amazon.com'>support@amazon.com</a></p><p>Phone: +1 888-280-4331</p><p>Website: <a href='https://www.amazon.com'>www.amazon.com</a></p><br><p style='font-size:12px;color:#888;'>This is an automated message. Please do not reply.</p></body></html>",
"dateTime": "2025-04-13T21:01:00",
"timezone": "Asia/Kolkata"
}

GET URL: 

