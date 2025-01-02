# ChattingApp

ChattingApp is a Java-based chat application that allows users to communicate with each other in real-time. The application supports user registration, login, sending and receiving messages, managing friends, and handling friend requests.

## Features

- User registration and login
- Real-time messaging
- Friend management (add, delete friends)
- Friend requests (send, accept, reject)
- Online/offline status indication
- Message history

## Project Structure

The project is organized into the following packages:

- `org.example`: Contains the main server and client classes.
- `org.example.controller`: Contains the controllers for handling user, message, and friend request operations.
- `org.example.gui`: Contains the GUI classes for the login, signup, and chat screens.
- `org.example.models`: Contains the model classes representing the data entities.
- `org.example.util`: Contains utility classes for database connection and other common functionalities.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven
- MySQL or any other relational database

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/ChattingApp.git
   cd ChattingApp
   ```

2. Configure the database connection:

   Update the `hibernate.cfg.xml` file in the `src/main/resources` directory with your database connection details.

3. Build the project using Maven:

   ```bash
   mvn clean install
   ```

## Running the Application

### Starting the Server

1. Navigate to the `target` directory:

   ```bash
   cd target
   ```

2. Run the server:

   ```bash
   java org.example.ChatServer
   ```

### Running the Client

1. Open a new terminal and navigate to the `target` directory:

   ```bash
   cd target
   ```

2. Run the client:

   ```bash
   java org.example.APP.java
   ```

## Usage

1. **Signup**: Open the client application and click on the "Signup" button to create a new user account.
2. **Login**: Enter your username and password to log in to the chat application.
3. **Chat**: After logging in, you can send and receive messages, manage friends, and handle friend requests.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.