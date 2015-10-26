package app;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.CRC32;

public class FSwalker {

    public enum Mode {
        CREATE, CHECK
    }

    private ByteBuffer buffer;
    private CRC32 crc32;

    public FSwalker(int size) {
        this.buffer = ByteBuffer.allocate(size);
        this.crc32 = new CRC32();
    }

    public void walk(FSnapshot snapshot, Mode mode) throws IOException {
        Queue<FSnapshot> walkQueue = new LinkedList<>();
        walkQueue.add(snapshot);
        while (!walkQueue.isEmpty()) {
            FSnapshot current_fs = walkQueue.poll();
            try {
                Files.list(current_fs.getRoot()).forEach(p -> {
                    if (Files.isDirectory(p)) {
                        walkQueue.add(processDirectory(p, current_fs));
                    } else {
                        processFile(p, current_fs);
                    }
                });
            } catch (AccessDeniedException e) {
                System.err.println(String.format("Access to next dir was denied %s", e.getFile()));
            }
            if (mode == Mode.CHECK) {
                current_fs.checkFileExists();
                current_fs.checkDirsExists();
            }
        }
    }


    private String msg = "Next %s was %s : %s";

    private FSnapshot processDirectory(Path path, FSnapshot parent) {
        if (!parent.isContainDir(path))
            System.out.println(String.format(msg, "directory", "encountered", path.toString()));
        return parent.addDirectory(path);
    }

    private void processFile(Path path, FSnapshot parent) {
        try {
            long size = Files.size(path);
            long hash_value;
            if (parent.isContainFile(path)) {
                FileDesc desc = parent.getFileDesc(path);
                if (size == desc.getSize()) {
                    hash_value = hashcalc(path, buffer, crc32);
                    if (hash_value == desc.getHash())
                        return;
                }
                System.out.println(String.format(msg, "file", "modified", path.toString()));
            } else {
                System.out.println(String.format(msg, "file", "encountered", path.toString()));
                hash_value = hashcalc(path, buffer, crc32);
                FileDesc desc = new FileDesc(path, hash_value, size);
                parent.addFile(path, desc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static long hashcalc(Path path, ByteBuffer buffer, CRC32 crc32) throws IOException {
        crc32.reset();
        try (FileChannel fileChannel = FileChannel.open(path)) {
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                crc32.update(buffer);
                buffer.clear();
            }
            return crc32.getValue();
        } catch (AccessDeniedException e) {
            System.err.println(String.format("Access to next file was denied %s", e.getFile()));
        }
        return -1;
    }

}
