import org.junit.Test;
import util.PathExtractor;

import static org.junit.Assert.assertEquals;

public class PathExtractorTest {
    private PathExtractor subject = new PathExtractor();

    @Test
    public void testCorrectLastPathGetsExtracted() {
        String result = subject.getLastPath("hello/world");
        assertEquals("world", result);
    }

    @Test
    public void testCorrectLastPathGetsExtractedWithMultiplePaths() {
        String result = subject.getLastPath("hello/world/how/are/you/doing");
        assertEquals("doing", result);
    }

    @Test
    public void testRemovesLastPath() {
        String result = subject.removeLastPath("hello/world/how/are/you/doing");
        assertEquals("hello/world/how/are/you", result);
    }

    @Test
    public void testIfOnlyComponentInPathThenItEmptiesIt() {
        String result = subject.removeLastPath("root");
        assertEquals("", result);
    }
}
