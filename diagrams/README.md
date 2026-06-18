# UML Diagrams - Rwanda National Digital Library Management System

This directory contains all UML diagrams for the Library Management System in PlantUML format.

## Diagrams Included

### 1. Use Case Diagram (`use-case-diagram.puml`)
Shows interactions between actors (Librarian, Member, Database) and system use cases.

**Key Features:**
- 8 main use cases
- Actor relationships
- Include/Extend relationships
- Business rules annotations

### 2. Class Diagram (`class-diagram.puml`)
Complete object-oriented design showing all classes, their attributes, methods, and relationships.

**Key Components:**
- Model classes (Book, Member)
- Service layer (LibraryService)
- Database layer (DatabaseConnection)
- Menu system (LibraryMenu)
- Task layer (BorrowTask)
- Interfaces (Runnable, InputValidator)

### 3. ER Diagram (`er-diagram.puml`)
Database schema showing entities, attributes, and relationships.

**Tables:**
- books (ISBN as PK)
- members (MemberID as PK)
- borrowing_records (RecordID as PK, with FKs)

### 4. Sequence Diagram - Borrow Book (`sequence-diagram-borrow.puml`)
Detailed interaction flow for the borrowing operation.

**Shows:**
- User interaction
- Input validation
- Database queries
- Transaction management
- Error handling paths
- Thread synchronization

### 5. Activity Diagram (`activity-diagram.puml`)
Complete workflow of the system showing all menu options and their processes.

**Covers:**
- All 8 menu operations
- Decision points
- Concurrent processing (parallel flows)
- Validation logic
- Error handling

### 6. Deployment Diagram (`deployment-diagram.puml`)
Physical architecture showing hardware nodes and software components.

**Components:**
- Client Workstation (Terminal)
- Application Server (Java Runtime)
- Database Server (PostgreSQL)
- Network connections
- Configuration details

## How to View These Diagrams

### Option 1: Online PlantUML Editor
1. Go to http://www.plantuml.com/plantuml/uml/
2. Copy the content of any `.puml` file
3. Paste into the editor
4. View the rendered diagram

### Option 2: VS Code Extension
1. Install "PlantUML" extension by jebbs
2. Open any `.puml` file
3. Press `Alt+D` to preview

### Option 3: IntelliJ IDEA Plugin
1. Install "PlantUML integration" plugin
2. Open any `.puml` file
3. The diagram renders automatically in the side panel

### Option 4: Command Line (requires PlantUML jar)
```bash
java -jar plantuml.jar diagrams/*.puml
```

This generates PNG images for all diagrams.

## Diagram Formats

All diagrams can be exported to:
- **PNG** - Raster image format
- **SVG** - Vector image format (recommended for documentation)
- **PDF** - Portable document format
- **LaTeX** - For academic papers

## Task Requirements Mapping

These diagrams fulfill all project requirements:

✅ **Task 1 (Database Schema)**: ER Diagram  
✅ **Task 2 (Encapsulation)**: Class Diagram  
✅ **Task 3 (Collections & Rules)**: Class Diagram, Activity Diagram  
✅ **Task 4 (Multithreading)**: Sequence Diagram, Class Diagram, Deployment Diagram  
✅ **Task 5 (JDBC)**: Sequence Diagram, ER Diagram, Deployment Diagram  

## Additional Documentation

All diagrams include:
- Detailed notes and annotations
- Business rules
- Technical constraints
- Implementation details

## Updating Diagrams

To modify any diagram:
1. Edit the corresponding `.puml` file
2. Use PlantUML syntax (see http://plantuml.com)
3. Validate the syntax before committing
4. Regenerate images if needed

## PlantUML Syntax Reference

- `@startuml` / `@enduml` - Document markers
- `class`, `interface`, `entity` - Define elements
- `-->`, `..|>`, `*--` - Different relationship types
- `note left/right/top/bottom` - Add annotations
- `actor`, `usecase` - Use case diagram elements
- `participant`, `activate` - Sequence diagram elements
- `start`, `stop`, `if`, `switch` - Activity diagram elements
- `node`, `component`, `database` - Deployment diagram elements
