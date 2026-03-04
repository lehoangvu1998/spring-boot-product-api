# Sử dụng môi trường JRE nhẹ gọn để chạy ứng dụng
FROM eclipse-temurin:17-jre-jammy

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Copy trực tiếp file JAR đã được GitHub Actions build xong (nằm ở thư mục target) vào image
COPY target/*.jar app.jar

# Mở cổng mà ứng dụng Spring Boot lắng nghe
EXPOSE 8080

# Lệnh mặc định để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]