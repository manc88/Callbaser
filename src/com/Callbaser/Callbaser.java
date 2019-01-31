package com.Callbaser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;




public class Callbaser {

    public static PrintService ps = PrintUtility.findPrintService(Config.PRINTSERVICE);

    public static void main(String[] args) throws IOException, InterruptedException{

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
                printPDF(Config.MONITORFOLDER + "\\" + event.context().toString());
            }
            key.reset();
        }

        Clog.info("Stoped");
    }



    public static boolean printPDF(String filePath){

        try {

            File file = new File(filePath);
            PDDocument doc = PDDocument.load(file);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(ps);
            job.setPageable(new PDFPageable(doc));
            job.print();
            Clog.info("File printed no service: " + ps.getName());

        } catch (PrinterException e) {
            e.printStackTrace();
            Clog.warn("Print failed",e);
            return false;
        }catch (IOException e){
            Clog.warn("File IO exception",e);
            return false;
        }catch (Exception e){
            Clog.warn("Unknown exception",e);
            return false;
        }

        return true;

    }



}

