package com.company;

import java.util.List;

public class Process {

    private List<Page> pages;

    public Process(MemoryManagement memoryManagement) {
        pages = memoryManagement.countMemory(this);
    }

    public String print(MemoryManagement memoryManagement) {
        String str = "";
        if (memoryManagement.isPageIsDeleted()) {
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

    public List<Page> getPages() {
        return pages;
    }
}
