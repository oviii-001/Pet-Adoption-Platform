# Pet Adoption Platform

A desktop application for finding and adopting pets.

## Features

*   **User Login:** Secure login for adopters and administrators.
*   **Browse Pets:** View a list of available pets with photos and brief details.
*   **Pet Details:** See more information about a specific pet.
*   **Adoption Preferences:** Set preferences to find pets that match your lifestyle.
*   **Application Status:** Track the status of your adoption applications.
*   **Admin Panel:** Manage pets and review adoption applications.

## Technologies Used

*   **Java:** Core application logic.
*   **JavaFX:** Graphical User Interface (GUI).
*   **Maven:** Build automation and dependency management.
*   **SQLite:** For the application database.

## Prerequisites

*   Java Development Kit (JDK) 22 or later.
*   Apache Maven.

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://example.com/your-repo.git
    cd Pet-Adoption-Platform/PetAdoptionPlatform
    ```

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn javafx:run
    ```

## Database

The application uses a SQLite database named `pet_adoption.db` located in the root of the `PetAdoptionPlatform` directory.

## Project Structure

```
PetAdoptionPlatform/
├── src/                # Source code
│   ├── Main.java       # Main entry point
│   ├── controller/     # Application logic
│   ├── model/          # Data models and database logic
│   └── view/           # GUI components (JavaFX)
├── resources/          # Images, icons, and other assets
├── lib/                # Local libraries
├── diagram/            # UML diagrams
└── pom.xml             # Maven project configuration
```