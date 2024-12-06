# Data-Processing-and-Storage-Assignment
Title: In-Memory Transactional Key-Value Store

Overview:
This project implements an in-memory key-value store with basic transaction support. The database supports the following operations:

begin_transaction()
put(key, value)
get(key)
commit()
rollback()
Key Features:

Values are stored as integers and keys as strings.
put() can only be invoked when a transaction is active.
get() can be invoked anytime and only reflects committed data.
Transactions are atomic: changes are only visible after commit().
rollback() reverts to the state before the transaction started.
How to Run:

Ensure you have Java 8 or later installed.

Clone the repository:

bash
Copy code
git clone <your_github_repository_url.git>
cd <repository_directory>
Compile and run:

bash
Copy code
javac InMemoryDBImpl.java
java InMemoryDBImpl
You should see console outputs demonstrating the behavior as per the example in the code.

Testing the Code:
You can modify the main() method or write your own tests to verify that transactions behave as expected. Uncomment the sections that throw errors to see exception handling in action.

Suggested Future Improvements (for an "official" assignment):

Clarify Transaction Visibility Rules: The current assignment states that get() should not reflect uncommitted changes. Future assignments might explicitly require a get() to return uncommitted values if called within the same transaction, but still hide them from outside transactions.
Extended Error Handling: Add clearer error messages and define custom exception classes for transaction errors.
Additional Methods: Consider adding methods like delete(key) or containsKey(key), and clarify how these interact with transactions.
Concurrency Considerations: For future iterations, specify behavior under concurrent transactions, or require the student to implement transaction isolation levels.
Automated Testing and Grading: Provide a suite of unit tests that can be run automatically. Students would pass the assignment by making all tests green. This would make grading more transparent and efficient.
