package test;


import app.FSnapshot;
import app.FSwalker;
import app.SnapStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FSWalkerTest {

    private Path root = Paths.get("/home/notkeo/Загрузки");
    private SnapStore snapStore;
    private FSwalker fSwalker;

    @Before
    public void before() {
        snapStore = new SnapStore();
        fSwalker = new FSwalker(1024);
    }

    @Test
    public void createFS() throws IOException {
        long before = -System.currentTimeMillis();
        FSnapshot fs = snapStore.getFS(root);
        fSwalker.walk(fs, FSwalker.Mode.CREATE);
        System.out.println(System.currentTimeMillis() + before);
    }



}
