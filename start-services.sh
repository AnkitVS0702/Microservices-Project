#!/bin/bash

# Function to stop all services started by this script
cleanup() {
    echo "Stopping all services..."
    # Kill background maven/java processes
    pkill -P $$
    exit 0
}

# Run cleanup on script interruption (Ctrl+C)
trap cleanup SIGINT SIGTERM

echo "==========================================="
echo "Starting E-Commerce Microservices Stack"
echo "==========================================="

# Create logs directory if it doesn't exist
mkdir -p logs

echo "1. Starting Discovery Server..."
nohup ./mvnw spring-boot:run -pl discovery-server > logs/discovery-server.log 2>&1 &
echo "Discovery Server PID: $!"

echo "Waiting 15 seconds for Discovery Server to be fully up..."
sleep 15

# Start other microservices
SERVICES=(
  "api-gateway"
  "product-service"
  "inventory-service"
  "order-service"
  "cart-service"
  "user-service"
  "payment-service"
  "notification-service"
)

for SERVICE in "${SERVICES[@]}"; do
  echo "Starting $SERVICE..."
  nohup ./mvnw spring-boot:run -pl "$SERVICE" > "logs/$SERVICE.log" 2>&1 &
  echo "$SERVICE started in background (PID: $!). Log saved to logs/$SERVICE.log"
  sleep 3 # Pause slightly between launches to reduce CPU spikes
done

echo "==========================================="
echo "All microservices are starting up!"
echo "Check progress by viewing log files in the 'logs/' folder."
echo "Example: tail -f logs/api-gateway.log"
echo "Press Ctrl+C to terminate all services started by this script."
echo "==========================================="

# Keep script running to allow trap cleanup
wait
