package util;

public class PathExtractor {

    public String getLastPath(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return path.substring(lastSlashIndex + 1, path.length());
    }

    public String removeLastPath(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return path.substring(0, lastSlashIndex);
    }
}
