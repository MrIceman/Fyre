import com.google.firebase.database.FirebaseDatabase;
import data.DataManagerImpl;
import data.FirebaseManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import util.FyreLogger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    @Mock
    private FirebaseDatabase database;
    private FirebaseManager firebaseManagerMock;
    DataManagerImpl subject = new DataManagerImpl(firebaseManagerMock);

    @Test
    public void simpleTest() {
        firebaseManagerMock = mock(FirebaseManager.class);
        database = mock(FirebaseDatabase.class);
        when(firebaseManagerMock.getDatabase()).thenReturn(database);
    }


}
