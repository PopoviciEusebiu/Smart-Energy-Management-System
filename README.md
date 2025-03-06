# Smart Energy Management System

## Overview

The Smart Energy Management System is a full-stack application that allows administrators and clients to manage and monitor smart energy metering devices. It consists of multiple microservices, a frontend application, a monitoring system, and a chat component for communication.

# Features

## 1. User Management Microservice

- Handles user authentication and authorization.

- Provides CRUD operations for users.

- Assigns roles (Administrator/Client) to users.

- Uses Spring Security OAuth2 JWT for secure authentication.

## 2. Device Management Microservice

- CRUD operations for smart energy metering devices.

- Maps users to devices.

- Synchronization mechanism between databases for user-device associations.

## 3. Monitoring and Communication Microservice

- Collects real-time data from smart metering devices via RabbitMQ.

- Processes and stores hourly energy consumption in the database.

- Uses WebSockets to notify users if energy consumption exceeds maximum limits.

## 4. Smart Metering Device Simulator

- Simulates a smart meter by reading energy data from a CSV file.

- Sends data in JSON format to RabbitMQ message queue.

- Uses a configuration file to set device ID.

## 5. Chat Microservice

- Enables real-time chat between users and administrators.

- WebSockets-based message exchange.

- Notifications for message read status and typing indicators.

- Supports multiple simultaneous chat sessions.

## 6. Authorization Component

- Integrated into User Management Microservice.

- Secures access to microservices using Spring Security OAuth2 JWT.

- Technologies Used

- Backend: Java Spring Boot (REST API), Spring Security, OAuth2, JWT, WebSockets, RabbitMQ

- Frontend: React.js

- Database: MySQL

- Messaging: RabbitMQ

- Authentication & Authorization: Spring Security OAuth2 JWT

- Containerization & Deployment: Docker, Nginx, Load Balancer

- Communication: WebSockets

# Deployment

- Dockerized services

- Nginx Load Balancer

- Reverse Proxy to manage microservices.

- SSL for Secure Communication
