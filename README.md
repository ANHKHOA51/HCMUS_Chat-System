# HCMUS Chat App

A JavaFX desktop application for real-time messaging, built with Java 21 and PostgreSQL.

## Tech Stack

*   **Language**: Java 21
*   **GUI Framework**: JavaFX 21
*   **Database**: PostgreSQL
*   **Build Tool**: Maven

## Prerequisites

Ensure you have the following installed:

*   [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
*   [Maven](https://maven.apache.org/)
*   [PostgreSQL](https://www.postgresql.org/download/)

## Setup & installation

### 1. Clone the repository
```bash
git clone https://github.com/ANHKHOA51/HCMUS_Chat-System.git
cd HCMUS_Chat-app
```

### 2. Database Setup
1.  Create a new PostgreSQL database (e.g., `chat_app`).
2.  Run the `script.sql` file located in the project root to initialize the database schema and sample data.
    ```bash
    psql -U your_postgres_user -d chat_app -f script.sql
    ```
    *(Or use a GUI tool like pgAdmin / DBeaver to execute the script)*

### 3. Environment Configuration
Create a `.env` file in the root directory of the project (`/HCMUS_Chat-app/.env`) and add the following variables:

```properties
# Database Configuration
SUPABASE_JDBC_URL=jdbc:postgresql://localhost:5432/chat_app
SUPABASE_DB_USER=your_postgres_user
SUPABASE_DB_PASS=your_postgres_password

# Email Service (for password reset)
GMAIL=your_email@gmail.com
APP_PASS=your_google_app_password

# AI Features (Optional)
OPENROUTER_API_KEY=your_openrouter_api_key
```

> **Note**: For `APP_PASS`, you need to generate an App Password from your Google Account settings if you are using 2-Step Verification.

## Usage

### 1. Run Chat Server (Required First)
The chat server acts as the backend for real-time messaging. It must be running before clients connect.
Open a terminal and run:
```bash
mvn exec:java@server
```

### 2. Run Applications
Open new terminal windows for each application instance.

#### Run Admin App
```bash
mvn javafx:run@admin
```

#### Run User App
```bash
mvn clean javafx:run
```

## Build & Deploy (Production)

### 1. Build Executable JARs
To build the application for all platforms (Windows, macOS, Linux):

```bash
mvn clean package -Pcross-platform
```

This will generate the following files in the `target/` directory:
*   `chat-server-1.0-SNAPSHOT.jar`
*   `admin-app-1.0-SNAPSHOT.jar`
*   `user-app-1.0-SNAPSHOT.jar`

### 2. Run Built JARs
**Chat Server:**
```bash
java -jar target/chat-server-1.0-SNAPSHOT.jar
```

**Admin App:**
```bash
java -jar target/admin-app-1.0-SNAPSHOT.jar
```

**User App:**
```bash
java -jar target/user-app-1.0-SNAPSHOT.jar
```

### 3. External Configuration (.env) for JARs
The application supports **Hierarchical Configuration Loading**, allowing you to override specific settings without rebuilding the JAR.

1.  **Defaults**: The JAR includes an embedded `.env` with base settings.
2.  **Override**: Place a `.env` file in the **same directory** as the JAR file. The app will prioritize values from this external file.

**Example Override `.env` (Full Template):**
```properties
# --- Server Configuration ---
# Hostname/IP to bind the Chat Server (default: 0.0.0.0 for server, localhost for clients)
CHAT_SERVER_HOST=localhost
# Port for WebSocket connection (default: 8887)
CHAT_SERVER_PORT=8887

# --- Database Configuration ---
SUPABASE_JDBC_URL=jdbc:postgresql://localhost:5432/chat_app
SUPABASE_DB_USER=your_postgres_user
SUPABASE_DB_PASS=your_postgres_password

# --- Email Service (Password Reset) ---
GMAIL=your_email@gmail.com
APP_PASS=your_google_app_password

# --- AI Features (Optional) ---
OPENROUTER_API_KEY=your_openrouter_api_key
```

### Default Credentials
The `script.sql` initializes the following accounts for testing:

*   **Standard User:**
    *   Username: `user`
    *   Password: `123`

*   **Administrator:**
    *   Username: `admin`
    *   Password: `123`

## Troubleshooting

*   **Database Connection Failed**: Ensure PostgreSQL is running and the credentials in `.env` match your local setup.
*   **API Key Missing**: If AI features don't work, check if `OPENROUTER_API_KEY` is set correctly in `.env`.