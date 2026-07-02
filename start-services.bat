@echo off
title E-Commerce Microservices Orchestrator
echo ===========================================
echo Starting E-Commerce Microservices Stack
echo ===========================================

REM Start Discovery Server first
echo 1. Starting Discovery Server...
start "discovery-server" cmd /c "mvnw.cmd spring-boot:run -pl discovery-server"

echo Waiting 15 seconds for Discovery Server to initialize...
timeout /t 15 /nobreak

REM Start the rest of the services in separate windows
echo 2. Starting API Gateway...
start "api-gateway" cmd /c "mvnw.cmd spring-boot:run -pl api-gateway"
timeout /t 2 /nobreak

echo 3. Starting Product Service...
start "product-service" cmd /c "mvnw.cmd spring-boot:run -pl product-service"
timeout /t 2 /nobreak

echo 4. Starting Inventory Service...
start "inventory-service" cmd /c "mvnw.cmd spring-boot:run -pl inventory-service"
timeout /t 2 /nobreak

echo 5. Starting Order Service...
start "order-service" cmd /c "mvnw.cmd spring-boot:run -pl order-service"
timeout /t 2 /nobreak

echo 6. Starting Cart Service...
start "cart-service" cmd /c "mvnw.cmd spring-boot:run -pl cart-service"
timeout /t 2 /nobreak

echo 7. Starting User Service...
start "user-service" cmd /c "mvnw.cmd spring-boot:run -pl user-service"
timeout /t 2 /nobreak

echo 8. Starting Payment Service...
start "payment-service" cmd /c "mvnw.cmd spring-boot:run -pl payment-service"
timeout /t 2 /nobreak

echo 9. Starting Notification Service...
start "notification-service" cmd /c "mvnw.cmd spring-boot:run -pl notification-service"

echo ===========================================
echo All microservices have been launched!
echo Each service is running in its own Command Prompt window.
echo Close individual windows to stop a service.
echo ===========================================
pause
