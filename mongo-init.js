// mongo-init.js
// This script runs once when the MongoDB container is first initialised.
// It creates a dedicated user with read/write access to the taskdb database.

db = db.getSiblingDB('taskdb');

db.createUser({
  user: 'taskuser',
  pwd: 'taskpassword',
  roles: [
    {
      role: 'readWrite',
      db: 'taskdb',
    },
  ],
});

// Create the tasks collection with a basic validator
db.createCollection('tasks', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['title', 'status'],
      properties: {
        title: {
          bsonType: 'string',
          description: 'Task title is required',
        },
        description: {
          bsonType: 'string',
        },
        status: {
          bsonType: 'string',
          enum: ['TODO', 'IN_PROGRESS', 'DONE'],
          description: 'Status must be TODO, IN_PROGRESS, or DONE',
        },
        subTasks: {
          bsonType: 'array',
        },
      },
    },
  },
});

print('MongoDB initialised: taskdb database and taskuser created successfully.');