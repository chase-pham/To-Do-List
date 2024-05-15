USE ToDoListDB;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Tasks')
BEGIN
    CREATE TABLE Tasks (
        TaskID INT PRIMARY KEY IDENTITY(1,1),
        TaskNAME VARCHAR(255) NOT NULL,
        Description TEXT,
        DueDate DATE,
        Priority INT DEFAULT 1,
        CompletionStatus BIT DEFAULT 0
    );
END
GO

-- If the table exists, alter it to add missing columns (only if they do not already exist)
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Tasks')
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'Priority' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD Priority INT DEFAULT 1;
    END;

    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'CompletionStatus' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD CompletionStatus BIT DEFAULT 0;
    END;

    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'DueDate' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD DueDate DATE;
    END;
END
GO

-- Grant permissions to 'TestUser'
GRANT INSERT, SELECT, UPDATE, DELETE ON dbo.Tasks TO TestUser;
GO
