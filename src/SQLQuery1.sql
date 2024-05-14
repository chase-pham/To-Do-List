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
-- Alter the 'Tasks' table to add enew columns if it already exists
ALTER TABLE Tasks
ADD Priority INT DEFAULT 1, -- Adding priority column with default value 1
	CompletionStatus BIT DEFAULT 0; -- Adding CompletionStatus column with default value 0 (incomplete)
GO

-- Grant permissions to 'TestUser'
GRANT INSERT, SELECT, UPDATE, DELETE ON dbo.Tasks TO TestUser;
GO