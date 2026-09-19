#!/bin/bash

# Navigate to your project's src/main/java directory
# Using your actual project path: /Users/peteratito/Documents/school-management-system/src/main/java/com/school

# Create package directories
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/security
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/config
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/exception
mkdir -p /Users/peteratito/Documents/school-management-system/src/main/java/com/school/util

echo "Package structure created successfully!"

# ===== ENTITY CLASSES =====
echo "Creating Entity classes..."

# Create Entity classes
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/User.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Role.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/ERole.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Student.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Teacher.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/SchoolClass.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Subject.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Attendance.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/AttendanceStatus.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/Grade.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/FeePayment.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/PaymentStatus.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/TimeTable.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/entity/DayOfWeek.java

# ===== REPOSITORY INTERFACES =====
echo "Creating Repository interfaces..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/UserRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/StudentRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/TeacherRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/RoleRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/SchoolClassRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/SubjectRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/AttendanceRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/GradeRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/FeePaymentRepository.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/repository/TimeTableRepository.java

# ===== DTO CLASSES =====
echo "Creating DTO classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/SignupRequest.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/LoginRequest.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/JwtResponse.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/MessageResponse.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/StudentDTO.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/TeacherDTO.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/AttendanceDTO.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/dto/GradeDTO.java

# ===== SECURITY CONFIGURATION =====
echo "Creating Security classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/security/WebSecurityConfig.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/security/UserDetailsServiceImpl.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/security/AuthTokenFilter.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/security/AuthEntryPointJwt.java

# ===== UTILITY CLASSES =====
echo "Creating Utility classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/util/JwtUtils.java

# ===== SERVICE CLASSES =====
echo "Creating Service classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service/UserService.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service/StudentService.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service/TeacherService.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service/AttendanceService.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/service/GradeService.java

# ===== CONTROLLER CLASSES =====
echo "Creating Controller classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/AuthController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/StudentController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/TeacherController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/AttendanceController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/GradeController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/SchoolClassController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/SubjectController.java
touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/controller/TestController.java

# ===== CONFIGURATION CLASSES =====
echo "Creating Configuration classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/config/DataLoader.java

# ===== EXCEPTION HANDLING =====
echo "Creating Exception handling classes..."

touch /Users/peteratito/Documents/school-management-system/src/main/java/com/school/exception/GlobalExceptionHandler.java

echo "All files created successfully!"
echo ""
echo "Project structure:"
echo "/Users/peteratito/Documents/school-management-system/src/main/java/com/school/"
echo "├── entity/"
echo "├── repository/"
echo "├── dto/"
echo "├── security/"
echo "├── controller/"
echo "├── service/"
echo "├── config/"
echo "├── exception/"
echo "└── util/"
echo ""
echo "Total files created: $(find /Users/peteratito/Documents/school-management-system/src/main/java/com/school -name "*.java" 2>/dev/null | 
wc -l)"
