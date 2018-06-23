# Akka Mongo Example

This program creates documents in the local mongo db and uses akka actors to read from them.

## Requirements
Read a MongoDB collection containing say 1000 documents, ( the document can contain 5 or more fields- you are free to define the document) in a loop and accumulate the output of any 2 properties of the document.

Use separate Akka actors for the following:

BeginLoop

EndLoop

Loop Execution

Implement your own collection pool mechanism to manage the connections to the MongoDB.

If the loop execution completes successfully, the accumulated output should be returned as a JSON array.

If the loop execution fails, the accumulated output till that point, a count of how many records have been accumulated, the reason for failure should be returned.


## How to Build and Run
You can build the application using the following command:

```
mvn clean package
```

You can run the application using the class files:

```
cd target/classes
java akkamongo.Main

```

