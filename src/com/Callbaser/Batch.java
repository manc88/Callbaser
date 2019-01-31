package com.Callbaser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Batch {

    private boolean cleanup = false;
    private boolean skipNoFound = false;
    private String confFileName;
    private String folderPath;
    private boolean ready;
    private List<File> files = new ArrayList<>();
    private String fullConfFilePath;

    public boolean isReady() {
        return ready;
    }


    public Batch(String FileName, String path) {
        confFileName = FileName;
        folderPath = path;
        fullConfFilePath = Paths.get(path,confFileName).toString();
        load();
    }

    private void load() {
        File file = new File(fullConfFilePath);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String rawLine = sc.nextLine();
                String nextLine = rawLine.trim().toUpperCase();

                if (nextLine.equals("")) {
                    continue;
                }

                if (nextLine.contains("#CLEANUP")) {
                    cleanup = true;
                    continue;
                }
                if (nextLine.contains("#SKIPNOFOUND")) {
                    skipNoFound = true;
                    continue;
                }
                if (Utils.isPDF(nextLine)) {
                    Path path = Paths.get(folderPath, rawLine.trim());
                    files.add(new File(path.toUri()));
                }

            }
        } catch (Exception e) {
            Clog.warn("Loading batch error:" + this.confFileName, e);
            ready = false;
        }

        ready = !files.isEmpty();

    }

    public void print() {

        for (File f : files) {
            printPDF(f);
        }

        if (cleanup) {
            Utils.sleep(1000);
            cleanup();
        }

    }

    private void cleanup() {
        for (File f : files) {
            try {
                Files.delete(f.toPath());
            } catch (IOException e) {
                Clog.warn("Clean up error:", e);
            }
        }
    }

    private boolean printPDF(File file) {

        try {
            PDDocument doc = PDDocument.load(file);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(Config.ps);
            job.setPageable(new PDFPageable(doc));
            job.print();
            Clog.info("File printed on service: " + Config.ps.getName());

        } catch (PrinterException e) {
            e.printStackTrace();
            Clog.warn("Print failed", e);
            return false;
        } catch (IOException e) {
            Clog.warn("File IO exception", e);
            return false;
        } catch (Exception e) {
            Clog.warn("Unknown exception", e);
            return false;
        }

        return true;

    }

}
