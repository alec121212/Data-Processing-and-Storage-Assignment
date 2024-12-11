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
    private Map<String, Integer> committedData = new HashMap<>();
    
    private Map<String, Integer> transactionData = null;

    private Map<String, Integer> originalValues = null;

    private boolean inTransaction = false;

    @Override
    public Integer get(String key) {

        return committedData.get(key);
    }

    @Override
    public void put(String key, int val) {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress. Cannot put without a transaction.");
        }

        if (!originalValues.containsKey(key)) {
            originalValues.put(key, committedData.get(key));
        }
        
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
        for (Map.Entry<String, Integer> entry : transactionData.entrySet()) {
            if (entry.getValue() == null) {
                committedData.remove(entry.getKey());
            } else {
                committedData.put(entry.getKey(), entry.getValue());
            }
        }

        inTransaction = false;
        transactionData = null;
        originalValues = null;
    }

    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress. Cannot rollback.");
        }
        for (Map.Entry<String, Integer> entry : originalValues.entrySet()) {
            String key = entry.getKey();
            Integer oldVal = entry.getValue();
            if (oldVal == null) {
                committedData.remove(key);
            } else {
                committedData.put(key, oldVal);
            }
        }
        inTransaction = false;
        transactionData = null;
        originalValues = null;
    }

    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDBImpl();
        
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
