# Project EVA
*A capstone project for the subject, CSIT284: Object Oriented Programming II*

**Team RaGO🏃🏻**  
*Tayo'y babangon muli!*

```kt
var members = listOf(
    "Rago, Moen Malone",
    "Cornilla, Karl Phoenix",
    "Modequillo, Clark Vincent",
    "Galorio, Sydney",
    "Muli, John Jacob"
)
```

---

## 📌 Project Overview

**Project EVA** (Enhanced Visualizer for Algorithms) is an educational JavaFX desktop application designed to help students learn and practice data structures and algorithms through interactive visualizations and gamified challenges. The application bridges the gap between traditional algorithm visualizers and our professor's specific teaching methodology.

Project EVA features an adventure/fantasy-themed environment where the story follows an adventurer venturing through the DSA journey, making learning data structures and algorithms both engaging and educational.

---

## 🚀 Main Features

### 👤 1. User System
- **Login/Registration:** Username and password authentication
- **Progress Tracking:** Saves completion data for both *Academy* and *Conquest* modes

### 🧭 2. Navigation Structure
- **Two Primary Pages:**
    - **Academy Page (Learning)**
    - **Conquest Page (Challenges)**
- **Main Menu Selection button:** Always accessible from top navigation to return to the selection page

### 🔰 3. JVM (Jay Vince Mode)
- Professor-specific implementations of data structures and algorithms
- Features unique removal operations for trees that select the next greatest element (left-most node of the right subtree)
- Specially designed to align with Professor Jay Vince's teaching methodology
- Makes the application particularly valuable for current and future students of Sir Jay Vince

### 🔍 4. Data Structure Visualizer
- Interactive visualizations of various data structures
- Step-by-step algorithm execution

### 🤖 5. Chatbots
- General chatbot for overall assistance
- Specific chatbots for each data structure to handle inquiries

---

## 🛠️ Technical Implementation Highlights

- **OOP Framework:** Abstraction and inheritance for algorithm logic
- **Generics:** Type-safe data structure implementations
- **Multithreading:** Handles animation, timers, and background saves
- **Database (JDBC):** User data, progress, and high score tracking
- **Design Patterns:**
    - Observer (for visualization updates)
    - Factory (for algorithm instantiation)
    - Singleton (user session handling)
- **JavaFX UI:** Custom interactive components and visual elements

### 💻 Tech Stack
- **Language:** Java
- **IDE:** IntelliJ
- **Database:** MySQL
- **AI Integration:** OpenAI for chatbot functionality
- **Design Tools:** Figma for UI/UX

---

## 🎓 Academy Page

### 📚 Topic Selector
- Lessons:
    - Arrays
    - Linked Lists
    - Stacks
    - Queues and Deques
    - BST
    - HashTable

### 🔍 Visualizer Panel
- Interactive JavaFX canvas to animate algorithm behavior
- Controls:
    - Step-by-step execution
    - Speed adjustment
    - Pause/Play/Reset
- Accepts custom data input for dynamic learning

#### Data Structure Visualizers

##### Chatbot
- Utilizes OpenAI's models via OpenAI's API
- Reads and understands data structure visualizations, including:
  ArrayList, Stack, Queue, HashTable, Deque, and Binary Tree

##### Queue
- Visualizes operations such as:
  - enqueue(num)
  - dequeue()
  - front()
  - clear()

##### Deque
- Visualizes operations such as:
  - addFirst(num)
  - addLast(num)
  - removeFirst()
  - removeLast()
  - peekFirst()
  - peekLast()
  - clear()

##### BST (Binary Search Tree)
- Implemented JVM (Jay Vince Mode)
- Standard Successor (gets successor: max of left subtree)
- Implemented visual controls (pause, play, forward, and backward)
- Dynamic tree restructuring: tree adjusts dynamically based on the nodes

##### HashTable
- Linear Probing
- Quadratic Probing
- Separate Chaining

### 📝 Concept Notes Panel
- Curated course notes
- Key theory explanations
- Pseudocode and Java code snippets
- Quick-reference concept guides

### 💻 Code Snippets Panel
- Curated course notes
- Key theory explanations
- Pseudocode and Java code snippets
- Quick-reference concept guides

---

## ⚔️ Conquest Page

### 🎮 Spellbreakers

The Conquest mode features a game called "Spellbreakers" where players face challenges across different data structure domains:

#### ArrayList Conquest
- Progressive challenges testing ArrayList operations and concepts

#### Stack Conquest
- Challenges focused on Stack operations and LIFO principles

#### Queue Conquest
- Missions built around Queue operations and FIFO concepts

#### Deque Conquest
- Advanced levels exploring Deque operations and versatility

### 🌍 Progression System
- Map-based interface for navigating challenges
- Locked/unlocked levels based on completions
- Increasing difficulty and achievement rewards

---

## 🧪 Installation

To run **Project EVA** locally:

1. Clone the repository:
    ```bash
    git clone https://github.com/Clorkies/LockedIn
    ```

2. Navigate to the project directory:
    ```bash
    cd ProjectEVA
    ```

3. Open the project in your preferred IDE (IntelliJ IDEA or Eclipse).

4. Build and run the project:
    - Ensure JDK 11 or higher is installed.
    - Run the main application class from your IDE.

---

