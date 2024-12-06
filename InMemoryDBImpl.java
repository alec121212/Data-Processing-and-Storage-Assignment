import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

interface InMemoryDB {
    Integer get(String key);
    void put(String key, int val);
    void begin_transaction();
    void commit();
    void rollback();
}

public class InMemoryDBImpl implements InMemoryDB {
    // This will hold the committed state of the database (visible outside transactions)
    private Map<String, Integer> committedData = new HashMap<>();
    
    // This will hold the staged (uncommitted) changes within the current transaction, if any
    private Map<String, Integer> transactionData = null;

    // Used to track which keys were modified during the transaction so we know what to rollback
    // if needed. We will store the original values before modification.
    private Map<String, Integer> originalValues = null;

    // Flag to know if a transaction is in progress
    private boolean inTransaction = false;

    @Override
    public Integer get(String key) {
        // If not in a transaction, we only return committed data
        // If in a transaction, we still only show committed data 
        // until commit has been called. The problem statement 
        // requires that get() does not show uncommitted changes.
        return committedData.get(key);
    }

    @Override
    public void put(String key, int val) {
        if (!inTransaction) {
            // According to the requirements, putting without a transaction should cause an error
            throw new IllegalStateException("No transaction in progress. Cannot put without a transaction.");
        }

        // If this key is not already recorded in originalValues, it means this is the first time 
        // we are modifying it in this transaction, so we store its old value for potential rollback.
        if (!originalValues.containsKey(key)) {
            // Store the original committed value (or null if it didn't exist)
            originalValues.put(key, committedData.get(key));
        }
        
        // Update the transaction data with the new value
        transactionData.put(key, val);
    }

    @Override
    public void begin_transaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction already in progress.");
        }
        inTransaction = true;
        transactionData = new HashMap<>();
        originalValues = new HashMap<>();
    }

    @Override
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress. Cannot commit.");
        }
        // Apply all changes from transactionData to committedData
        for (Map.Entry<String, Integer> entry : transactionData.entrySet()) {
            if (entry.getValue() == null) {
                committedData.remove(entry.getKey());
            } else {
                committedData.put(entry.getKey(), entry.getValue());
            }
        }
        // End the transaction
        inTransaction = false;
        transactionData = null;
        originalValues = null;
    }

    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress. Cannot rollback.");
        }
        // Revert changes using originalValues
        for (Map.Entry<String, Integer> entry : originalValues.entrySet()) {
            String key = entry.getKey();
            Integer oldVal = entry.getValue();
            if (oldVal == null) {
                committedData.remove(key);
            } else {
                committedData.put(key, oldVal);
            }
        }
        // End the transaction
        inTransaction = false;
        transactionData = null;
        originalValues = null;
    }

    // For demonstration/testing purposes
    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDBImpl();
        
        // Should return null since A doesn't exist
        System.out.println("get(A) before transaction: " + db.get("A")); // null

        // Should throw an error (uncomment to test)
        // db.put("A", 5);

        // Begin a transaction
        db.begin_transaction();

        db.put("A", 5);
        // get(A) should still return null since not committed
        System.out.println("get(A) after put(A,5) in transaction: " + db.get("A")); // null

        db.put("A", 6);
        db.commit();

        // Now get(A) should return 6
        System.out.println("get(A) after commit: " + db.get("A")); // 6

        // Following should throw an error as no transaction is ongoing (uncomment to test)
        // db.commit();
        // db.rollback();

        // get(B) should return null as it doesn't exist
        System.out.println("get(B): " + db.get("B")); // null

        // Begin a new transaction
        db.begin_transaction();
        db.put("B", 10);
        
        // Rollback changes
        db.rollback();
        // get(B) should still return null
        System.out.println("get(B) after rollback: " + db.get("B")); // null
    }
}
