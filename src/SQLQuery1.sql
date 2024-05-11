USE ToDoListDB;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Tasks')
CREATE TABLE Tasks (
    TaskID INT PRIMARY KEY IDENTITY(1,1),
    TaskNAME VARCHAR(255) NOT NULL,
    Description TEXT,
    DueDate DATE,
    Status BIT
);
GO
GRANT INSERT, SELECT, UPDATE, DELETE ON dbo.Tasks TO TestUser;