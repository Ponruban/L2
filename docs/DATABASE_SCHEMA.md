# Database Schema

## 1. User
| Column Name | Data Type     | Constraints                        |
|-------------|--------------|------------------------------------|
| id          | SERIAL       | PRIMARY KEY                        |
| name        | VARCHAR(100) | NOT NULL                           |
| email       | VARCHAR(255) | NOT NULL, UNIQUE                   |
| role        | VARCHAR(50)  | NOT NULL                           |
| preferences | JSONB        |                                    |
| created_at  | TIMESTAMP    | NOT NULL, DEFAULT now()            |

**Relationships:**
- One-to-many with Comment, Attachment, TimeLog
- Many-to-many with Project (via ProjectMember)

---

## 2. Project
| Column Name   | Data Type      | Constraints                        |
|--------------|---------------|------------------------------------|
| id           | SERIAL        | PRIMARY KEY                        |
| name         | VARCHAR(150)  | NOT NULL, UNIQUE                   |
| description  | TEXT          |                                    |
| start_date   | DATE          | NOT NULL                           |
| end_date     | DATE          |                                    |
| status       | VARCHAR(50)   | NOT NULL                           |
| created_at   | TIMESTAMP     | NOT NULL, DEFAULT now()            |

**Relationships:**
- One-to-many with Milestone, Task
- Many-to-many with User (via ProjectMember)

---

## 3. ProjectMember (Junction Table)
| Column Name | Data Type  | Constraints                                 |
|-------------|-----------|---------------------------------------------|
| project_id  | INTEGER   | PRIMARY KEY, FOREIGN KEY → Project(id)      |
| user_id     | INTEGER   | PRIMARY KEY, FOREIGN KEY → User(id)         |
| role        | VARCHAR(50) | NOT NULL                                  |

**Relationships:**
- Many-to-many between User and Project

---

## 4. Milestone
| Column Name | Data Type     | Constraints                        |
|-------------|--------------|------------------------------------|
| id          | SERIAL       | PRIMARY KEY                        |
| project_id  | INTEGER      | NOT NULL, FOREIGN KEY → Project(id)|
| name        | VARCHAR(150) | NOT NULL                           |
| due_date    | DATE         |                                    |
| status      | VARCHAR(50)  | NOT NULL                           |

**Relationships:**
- Many-to-one with Project
- One-to-many with Task

---

## 5. Task
| Column Name   | Data Type      | Constraints                        |
|--------------|---------------|------------------------------------|
| id           | SERIAL        | PRIMARY KEY                        |
| project_id   | INTEGER       | NOT NULL, FOREIGN KEY → Project(id)|
| milestone_id | INTEGER       | FOREIGN KEY → Milestone(id)        |
| title        | VARCHAR(200)  | NOT NULL                           |
| description  | TEXT          |                                    |
| assignee_id  | INTEGER       | FOREIGN KEY → User(id)             |
| priority     | VARCHAR(20)   | NOT NULL                           |
| status       | VARCHAR(50)   | NOT NULL                           |
| deadline     | DATE          |                                    |
| created_at   | TIMESTAMP     | NOT NULL, DEFAULT now()            |

**Relationships:**
- Many-to-one with Project, Milestone, User
- One-to-many with Comment, Attachment, TimeLog

**Indexes:**
- INDEX on (project_id, status)
- INDEX on assignee_id

---

## 6. Comment
| Column Name | Data Type   | Constraints                        |
|-------------|------------|------------------------------------|
| id          | SERIAL     | PRIMARY KEY                        |
| task_id     | INTEGER    | NOT NULL, FOREIGN KEY → Task(id)   |
| user_id     | INTEGER    | NOT NULL, FOREIGN KEY → User(id)   |
| content     | TEXT       | NOT NULL                           |
| timestamp   | TIMESTAMP  | NOT NULL, DEFAULT now()            |

**Relationships:**
- Many-to-one with Task, User

---

## 7. Attachment
| Column Name | Data Type     | Constraints                        |
|-------------|--------------|------------------------------------|
| id          | SERIAL       | PRIMARY KEY                        |
| task_id     | INTEGER      | NOT NULL, FOREIGN KEY → Task(id)   |
| file_url    | VARCHAR(255) | NOT NULL                           |
| uploaded_by | INTEGER      | NOT NULL, FOREIGN KEY → User(id)   |
| uploaded_at | TIMESTAMP    | NOT NULL, DEFAULT now()            |

**Relationships:**
- Many-to-one with Task, User

---

## 8. TimeLog
| Column Name | Data Type     | Constraints                        |
|-------------|--------------|------------------------------------|
| id          | SERIAL       | PRIMARY KEY                        |
| task_id     | INTEGER      | NOT NULL, FOREIGN KEY → Task(id)   |
| user_id     | INTEGER      | NOT NULL, FOREIGN KEY → User(id)   |
| hours       | NUMERIC(5,2) | NOT NULL, CHECK (hours >= 0)       |
| date        | DATE         | NOT NULL                           |

**Relationships:**
- Many-to-one with Task, User

**Indexes:**
- INDEX on (user_id, date)
- INDEX on (task_id, date)

---

## 9. TaskStatus (for customizable statuses)
| Column Name | Data Type     | Constraints                        |
|-------------|--------------|------------------------------------|
| id          | SERIAL       | PRIMARY KEY                        |
| project_id  | INTEGER      | NOT NULL, FOREIGN KEY → Project(id)|
| name        | VARCHAR(50)  | NOT NULL, UNIQUE (project_id, name)|
| is_default  | BOOLEAN      | NOT NULL, DEFAULT FALSE            |

**Relationships:**
- Many-to-one with Project

---

## Relationships Diagram (Textual)
- User ⟷ Project: many-to-many (via ProjectMember)
- Project ⟶ Milestone: one-to-many
- Project ⟶ Task: one-to-many
- Milestone ⟶ Task: one-to-many
- Task ⟶ Comment, Attachment, TimeLog: one-to-many
- Task ⟶ User (assignee): many-to-one
- Task ⟶ TaskStatus: many-to-one (if using status as FK)
- Comment, Attachment, TimeLog ⟶ User: many-to-one

---

# Example PostgreSQL DDL

```sql
-- User Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    preferences JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Project Table
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- ProjectMember Table
CREATE TABLE project_members (
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (project_id, user_id)
);

-- Milestone Table
CREATE TABLE milestones (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    due_date DATE,
    status VARCHAR(50) NOT NULL
);

-- TaskStatus Table
CREATE TABLE task_statuses (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (project_id, name)
);

-- Task Table
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    milestone_id INTEGER REFERENCES milestones(id) ON DELETE SET NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    assignee_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    deadline DATE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_tasks_project_status ON tasks(project_id, status);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);

-- Comment Table
CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT now()
);

-- Attachment Table
CREATE TABLE attachments (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    file_url VARCHAR(255) NOT NULL,
    uploaded_by INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    uploaded_at TIMESTAMP NOT NULL DEFAULT now()
);

-- TimeLog Table
CREATE TABLE time_logs (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hours NUMERIC(5,2) NOT NULL CHECK (hours >= 0),
    date DATE NOT NULL
);

CREATE INDEX idx_timelogs_user_date ON time_logs(user_id, date);
CREATE INDEX idx_timelogs_task_date ON time_logs(task_id, date);
```

---

# ER Diagram

See [docs/ER_DIAGRAM.md](./ER_DIAGRAM.md) for the full Entity-Relationship diagram in Mermaid format.