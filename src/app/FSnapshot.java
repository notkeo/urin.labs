package app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class FSnapshot {

    private Path root;
    private HashMap<Path, FSnapshot> dirs;
    private HashMap<Path, FileDesc> hashes;

    public FSnapshot(Path root) {
        this.root = root;
        this.hashes = new HashMap<>();
    }

    public boolean isContainDir(Path p) {

        return dirs != null && dirs.containsKey(p);
    }

    public boolean isContainFile(Path p) {
        return hashes != null && hashes.containsKey(p);
    }

    public FSnapshot addDirectory(Path path) {
        if (dirs == null) dirs = new HashMap<>();
        if (!dirs.containsKey(path)) {
            FSnapshot fSnapshot = new FSnapshot(path);
            dirs.put(path, fSnapshot);
            return fSnapshot;
        } else return dirs.get(path);
    }

    public void addFile(Path file, FileDesc descriptor) {
        if (hashes == null) hashes = new HashMap<>();
        hashes.put(file, descriptor);
    }

    public Path getRoot() {
        return root;
    }


    public FileDesc getFileDesc(Path filename) {
        return hashes.get(filename.getFileName().toString());
    }

    public void checkFileExists() {
        checkExists(hashes.keySet());
    }

    public void checkDirsExists() {
        checkExists(dirs.keySet());
    }

    private void checkExists(Set<Path> set) {
        for (Path key : set) {
            if (!Files.exists(key))
                System.out.println("Next entry was faded: " + key);
        }
    }


}
