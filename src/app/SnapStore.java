package app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class SnapStore {

    private HashMap<Path, FSnapshot> roots = new HashMap<>();

    public FSnapshot getFS(Path path) {
        Path root = path.getRoot();
        FSnapshot snapshot = roots.get(root);
        if (snapshot == null) {
            snapshot = new FSnapshot(root);
            roots.put(root, snapshot);
        }
        Iterator<Path> iterator = path.iterator();
        while (iterator.hasNext()) {
            root = root.resolve(iterator.next());
            if (Files.isDirectory(root))
                snapshot = snapshot.addDirectory(root);
            else
                return snapshot;
        }
        return snapshot;
    }
}
