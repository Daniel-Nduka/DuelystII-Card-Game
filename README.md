

 Overview

Welcome to the Game Application. This project is a turn-based strategy game where players can summon units, move them on a board, and engage in combat using various abilities and spells. The game features AI opponents, dynamic game states, and rich unit interactions.

 Table of Contents

- [Overview](overview)
- [Setup Instructions](setup-instructions)
- [Features](features)
- [Development Process](development-process)
- [Challenges](challenges)
- [Contributors](contributors)

 Setup Instructions

 Prerequisites

- Java Development Kit (JDK) 8 or higher
- Git
- Scala Build Tool (SBT)

 Installation

1. Clone the Repository
   ```sh
   git clone https://gitlab.com/team29/game-app.git
   cd game-app
   ```

2. Build the Project
   ```sh
   sbt compile
   ```

3. Run the Application
   ```sh
   sbt run
   ```

 Testing

To run the tests:
```sh
sbt test
```

 Features

 Core Features

1. Game Board and Game State Management
   - Initialization of the game board with interactive tiles.
   - Management of game states and transitions through the `GameController`.

2. Player and AI Interaction
   - Players can summon, move, and attack units.
   - AI players use decision trees to simulate a believable opponent.

3. Units and Abilities
   - Various unit types with unique abilities (e.g., Flying, Rush, Deathwatch).
   - Real-time updates of health, mana, and other unit attributes.

4. Spells and Effects
   - Implementation of multiple spells with different effects on units and the game board.

 Detailed Features per Sprint

 Sprint 1
- Basic game board setup and display.
- Initial player setup, including deck management and avatar spawning.
- Handling of front-end events through `GameController`.

 Sprint 2
- Implementation of unit spawning via a `UnitBuilder` class.
- Basic unit actions such as moving and attacking.
- End turn logic and mana/resource management.

 Sprint 3
- Development of unit abilities and event response mechanisms.
- Observer pattern for handling ability triggers like Deathwatch and Opening Gambit.
- Initial spell implementation with a `SpellBuilder` class.

 Sprint 4
- AI logic and behavior for sequential actions.
- Polishing and bug-fixing, including animations and effect implementations.
- Full integration of AI decision-making and game-state updates.

 Development Process

Our development process followed an agile methodology, with weekly sprints focusing on different aspects of the game:

1. Sprint 1: Establishing the game board and basic game state.
2. Sprint 2: Developing core unit functionalities and player interactions.
3. Sprint 3: Implementing unit abilities and spell effects.
4. Sprint 4: Finalizing AI behavior and overall game polish.

Each sprint included the following steps:
- Setting priorities and defining user stories.
- Implementing features and conducting testing.
- Addressing challenges and refining code through team collaboration.

 Challenges

 BufferOverflow Exception
- Encountered when drawing and changing tiles on the game board.
- Fixed by optimizing the number of commands sent to the browser.

 Git and Merging
- Initial difficulties with Git operations and merge conflicts.
- Resolved by improving Git practices and direct code sharing in meetings.

 Cross-Platform Issues
- Inconsistencies in file loading order between Windows and Mac.
- Fixed by sorting filenames before loading to ensure consistent order.

 Event Handling Bugs
- Infinite loops and incorrect event triggers.
- Resolved by refining event handling logic and implementing robust checks.

 Contributors

- Stephen: Initial values, attack values, spell implementation.
- Alasdair: Game board functionality, unit movement, spell effects.
- Murad: Avatar initialization, attack logic, AI behavior.
- Amanda: Health and mana updates, player notifications, spell casting.
- Nduka: Unit rendering, observer pattern implementation, AI decision making.


