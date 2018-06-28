import com.google.firebase.database.FirebaseDatabase;
import data.FirebaseManager;
import data.impl.DataManagerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import util.FyreLogger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    private FirebaseDatabase database;
    private FirebaseManager firebaseManagerMock;
    DataManagerImpl subject = new DataManagerImpl(new FyreLogger("testing"), firebaseManagerMock);

    @Test
    public void simpleTest() {
        firebaseManagerMock = mock(FirebaseManager.class);
        database = mock(FirebaseDatabase.class);
        when(firebaseManagerMock.getDatabase()).thenReturn(database);
    }


}
