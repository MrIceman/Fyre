import domain.DataManager;
import domain.VisualFire;
import org.mockito.Mock;
import util.FyreLogger;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class TestVisualFire {
    private String path = "";
    @Mock
    private DataManager dataManager;
    @Mock
    private FyreLogger logger;

    private VisualFire subject;

    public TestVisualFire() {
        subject = new VisualFire(path, dataManager, logger);
    }

    public void test_setUp() {
        subject.setUp();

        try {
            verify(dataManager).configureFirebase(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
