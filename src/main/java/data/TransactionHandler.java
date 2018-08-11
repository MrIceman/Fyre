package data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class TransactionHandler implements Transaction.Handler {
    @Override
    public Transaction.Result doTransaction(MutableData currentData) {
        return null;
    }

    @Override
    public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {

    }
}
