package com.company;

import java.util.List;
import java.util.Random;

public class Process {

    private List<Page> pages;
    private int tasksNumber;
    private final int memoryNumber;
    private int processNumber = 0;
    private boolean pageIsDeleted = false;

    public Process(PageTable pageTable) {
        Random rnd = new Random();
        processNumber++;
        tasksNumber = rnd.nextInt(3) + 4;
        memoryNumber = rnd.nextInt(64) + 64;
        pages = pageTable.countMemory(this);
    }

    public void performance() {
        Random rnd = new Random();
        if (tasksNumber > 0) {
            for (int i = 0; i < pages.size(); i++) {
                if (rnd.nextBoolean()) {
                    byte[] bytes = new byte[rnd.nextInt(PageTable.getSize())];
                    rnd.nextBytes(bytes);
                    pages.get(i).setBytes(rnd.nextInt(PageTable.getSize()), bytes);
                }
            }
            tasksNumber--;
        } else {
            for (int i = 0; i < pages.size(); i++) {
                pages.get(i).delete();
            }
            pageIsDeleted = true;
        }
    }

    public String print() {
        String str = "Процесс №" + processNumber + "\n";
        if (isPageDeleted()) {
            str += "Страницы:\n";
            for (int i = 0; i < pages.size(); i++) {
                str += pages.get(i).print();
            }
            str += "\n";
        }
        return str;
    }

    public String pagesInVirtualMemory() {
        String str = "";
        for (int i = 0; i < pages.size(); i++) {
            if (!pages.get(i).isInPhysicalMemory()) {
               str += pages.get(i).toString();
            }
        }
        return str;
    }

    public String getProcessNumber() {
        return String.valueOf(processNumber);
    }

    public int getMemoryNumber() {
        return memoryNumber;
    }

    public boolean isPageDeleted() {
        return pageIsDeleted;
    }

}
