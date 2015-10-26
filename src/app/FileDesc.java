package app;

import java.nio.file.Path;

/**
 * Created by notkeo on 26.10.15.
 */
public class FileDesc {

    private Path path;
    private long hash;
    private long size;


    public FileDesc(Path path, long size) {
        this.path = path;
        this.size = size;
    }

    public FileDesc(Path path, long hash, long size) {
        this(path, size);
        this.hash = hash;
    }

    public long getHash() {
        return hash;
    }

    public long getSize() {
        return size;
    }
}
