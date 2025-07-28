# Giai đoạn Build: Sử dụng một image OpenJDK với JDK để biên dịch ứng dụng
FROM openjdk:17-jdk-slim as builder

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Sao chép file Maven pom.xml để tải dependencies (giúp cache tốt hơn)
COPY pom.xml .

# Sao chép toàn bộ mã nguồn của bạn
COPY src ./src

# Biên dịch và đóng gói ứng dụng Spring Boot thành file JAR
# -DskipTests: bỏ qua chạy tests ở đây vì chúng ta sẽ chạy tests trong GitHub Actions riêng
RUN ./mvnw clean package -DskipTests

# Giai đoạn Runtime: Sử dụng một image OpenJDK chỉ với JRE để chạy ứng dụng (nhỏ gọn hơn)
FROM openjdk:17-jre-slim

# Đặt thư mục làm việc cho ứng dụng chạy
WORKDIR /app

# Sao chép file JAR đã build từ giai đoạn 'builder' vào image cuối cùng
COPY --from=builder /app/target/*.jar app.jar

# Mở cổng mà ứng dụng Spring Boot lắng nghe (mặc định là 8080)
EXPOSE 8080

# Lệnh mặc định để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]