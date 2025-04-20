# Project EVA
*A capstone project for the subject, CSIT284: Object Oriented Programming.*

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

---

## 🚀 Main Features

### 👤 1. User System
- **Login/Registration:** Username and password authentication
- **Profile Management:** One profile per user
- **Progress Tracking:** Saves completion data for both *Academy* and *Conquest* modes

### 🧭 2. Navigation Structure
- **Two Primary Modes:**
    - **Academy Mode (Learning)**
    - **Conquest Mode (Challenges)**
- **Home Button:** Always accessible from top navigation to return to the profiles page

---

## 🎓 Academy Mode (Learning)

### 📚 Topic Selector
- Categories:
    - Arrays
    - Linked Lists
    - Stacks & Queues
    - Trees
    - Graphs
    - Sorting Algorithms
- Progress indicators: Checkmarks or percentage complete

### 🔍 Visualizer Panel
- Interactive JavaFX canvas to animate algorithm behavior
- Controls:
    - Step-by-step execution
    - Speed adjustment
    - Pause/Play/Reset
- Accepts custom data input for dynamic learning

### 📝 Concept Notes Panel
- Curated course notes
- Key theory explanations
- Pseudocode and Java code snippets
- Quick-reference concept guides

---

## ⚔️ Conquest Mode (Challenges)

### 1. **Operation Command Game** *(Non-tree structures)*
- Inspired by **TyperShark**
- Match target data structure state using typed operations (e.g., `push()`, `pop()`)
- Threat indicator with time pressure mechanics
- Increasing difficulty per level

### 2. **Tree Builder Challenge** *(Tree structures)*
- Drag-and-drop style tree construction
- Conditions: e.g., "must be balanced", "follow traversal order"
- Scoring based on time and accuracy

### 🌍 Progression System
- Map-based interface for navigating challenges
- Locked/unlocked levels based on completions
- Increasing difficulty and achievement rewards

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

---

## 📅 Development Priorities

- Core visualization framework and basic algorithm implementations
- User authentication system and database connectivity
- Academy mode with initial algorithm set
- Conquest mode game mechanics and progression system
- UI polish and comprehensive algorithm coverage

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

