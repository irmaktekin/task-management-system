# Task Management System  

## Project Description  
The **Task Management System** is a backend application that allows users to efficiently manage projects and tasks. Users can create projects, assign tasks to team members, track progress, and update task statuses.  

## Technologies Used  
This project is built using the following technologies:  
- **Java 21**  
- **Spring Boot** (Spring Web, Spring Data JPA, Spring Security)  
- **Hibernate**  
- **PostgreSQL**  
- **Maven**
- **Jacoco**
- **Swagger**

## Features  
- **Project Management:** Create, update, and delete projects.  
- **Task Management:** Create, assign, update, and delete tasks.  
- **User Management:** Register users, authenticate, and assign tasks.  
- **Task Status Tracking:** Update task progress with different statuses.  

## Installation & Setup  

### Prerequisites  
Make sure you have the following installed:  
- **Java 21**  
- **Maven**  
- **PostgreSQL**

## Endpoints
### Task Controller
![image](https://github.com/user-attachments/assets/e588a940-9cf6-4433-bbba-5e39b5e8bab1)

### Project Controller
![image](https://github.com/user-attachments/assets/57e7571f-5f79-4be3-acbc-f5b02290d50a)

### User Controller
![image](https://github.com/user-attachments/assets/7f69f90d-25b5-401c-a73b-a17f25354024)

### Entity Relationship

- Project-Task -> One-to-Many
- Project-User -> Many-to-One
- User-Role -> Many-to-Many
- Task-Attachment -> Many-to-Many
- Task-Comment -> One-To-Many


### Steps to Run the Project  

1. **Clone the Repository**  
   ```sh
   git clone https://github.com/irmaktekin/task-management-system.git
   cd task-management-system

2. **Run Applicaiton**
   ```sh
   mvn spring-boot:run
