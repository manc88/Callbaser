package com.Callbaser;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;


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
        Clog.info("Start callbaser service at " + LocalDateTime.now());
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                String fileName = event.context().toString();
                if(Config.USEFILELIST){
                    if (Utils.isChl(fileName)) {
                        Batch batch = new Batch(fileName, Config.MONITORFOLDER);
                        if (batch.isReady()) {
                            batch.print();
                        } else {
                            Clog.warn("Batch not ready! " + fileName, new Exception());
                        }
                    }else if(Utils.isPDF(fileName)){
                        Batch batch = new Batch(new File(Paths.get(Config.MONITORFOLDER,fileName).toUri()));
                        batch.print();
                    }else{
                        Clog.warn("Unxpected file type" + fileName, new Exception());
                    }
                }
            }
            key.reset();
        }

        Clog.info("Stoped");
    }


}

