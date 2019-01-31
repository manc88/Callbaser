package com.Callbaser;


import java.io.IOException;
import java.nio.file.*;


public class Callbaser {

    public static void main(String[] args) throws IOException, InterruptedException {

        if(!Config.ready){
            System.err.println("Error loading config.");
            return;
        }

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(Config.MONITORFOLDER);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
                //StandardWatchEventKinds.ENTRY_DELETE,
                //StandardWatchEventKinds.ENTRY_MODIFY
        );
        Clog.info("Start callbaser service");
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                Clog.info("File detected: " + event.context().toString());
                if (Utils.isChl(event.context().toString())) {
                    Batch batch = new Batch(event.context().toString(), Config.MONITORFOLDER);
                    if (batch.isReady()) {
                        batch.print();
                    } else {
                        Clog.warn("Batch not ready! " + event.context().toString(), new Exception());
                    }
                }
            }
            key.reset();
        }

        Clog.info("Stoped");
    }


}

