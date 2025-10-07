# Student Attendance Management (Spring Boot)

## Features
- Admin login (session-based). Default admin provided in your DB script: `hansika / hansika`
- Add / Edit / Delete students
- Mark attendance (by date)
- Edit attendance records
- Attendance summary per student
- Dashboard with basic stats

## How to run
1. Ensure your Oracle DB is running and the provided SQL script has been executed (admin, student, attendance tables + sequences + triggers).
2. Update `src/main/resources/application.properties` with your Oracle DB username/password.
3. Build & run:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
4. Open http://localhost:8080 and login using the admin account.

Note: Hibernate ddl-auto is set to `none` because schema is managed by your SQL script.
