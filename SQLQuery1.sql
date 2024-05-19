USE ToDoListDB;
GO

-- Create the Tasks table if it doesn't exist
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

-- If the table exists, ensure all necessary columns are present
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Tasks')
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'Description' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD Description TEXT;
    END;

    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'DueDate' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD DueDate DATE;
    END;

    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'Priority' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD Priority INT DEFAULT 1;
    END;

    IF NOT EXISTS (SELECT * FROM sys.columns WHERE name = 'CompletionStatus' AND object_id = OBJECT_ID('Tasks'))
    BEGIN
        ALTER TABLE Tasks ADD CompletionStatus BIT DEFAULT 0;
    END;
END
GO

-- Grant permissions to 'TestUser'
GRANT SELECT, INSERT, UPDATE, DELETE ON Tasks TO TestUser;
GRANT ALTER, REFERENCES ON OBJECT::Tasks TO TestUser;
GRANT CREATE TABLE TO TestUser;
GRANT ALTER TO TestUser;
GRANT DROP TO TestUser;
GO
