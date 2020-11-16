package com.company;

import java.util.*;

public class MemoryManagement {

    private List<Process> processes;
    private PageTable pageTable;
    private int processesNumber;
    private int k;
    private boolean launch;
    private boolean pageIsDeleted;
    private int tasksNumber;

    public MemoryManagement() {
        Random rnd = new Random();
        processes = new ArrayList<Process>();
        processesNumber = rnd.nextInt(3) + 2;
        pageTable = new PageTable();
        k = 0;
        launch = false;
        pageIsDeleted = false;
        tasksNumber = rnd.nextInt(3) + 4;
    }

    public String performance() {
        String str = "";
        for (int i = 0; i < processesNumber; i++) {
            Process process = new Process(this);
            processes.add(process);
            str = "Создание процесса:" + "\n" + process.print(this) + "\n";
        }
        return str;
    }

    public String continuation() {
        if (processes.size() > 0) {
            if (k < processes.size()) {
                Process curProcess = processes.get(k);
                String str = "Выполнение:\n";
                if (launch) {
                        Random rnd = new Random();
                        if (tasksNumber > 0) {
                            for (int i = 0; i < curProcess.getPages().size(); i++) {
                                if (rnd.nextBoolean()) {
                                    byte[] bytes = new byte[rnd.nextInt(PageTable.getSize())];
                                    rnd.nextBytes(bytes);
                                    curProcess.getPages().get(i).setBytes(rnd.nextInt(PageTable.getSize()), bytes);
                                }
                            }
                            tasksNumber--;
                        } else {
                            for (int i = 0; i < curProcess.getPages().size(); i++) {
                                curProcess.getPages().get(i).delete();
                            }
                            pageIsDeleted = true;
                        }
                    k++;
                }
                launch = !launch;
                str += curProcess.print(this);
                if (pageIsDeleted) {
                    str += "Завершение работы процесса";
                    processes.remove(curProcess);
                }
                return str;
            } else {
                k = 0;
                return continuation();
            }
        } else {
            return "Выполнение всех процессов завершено";
        }
    }

    private List<Page> countPhysicalMemory(Process process, int pageCount) {
        List<Page> resultList = new ArrayList<Page>();
        for (int i = 0; i < pageCount; i++) {
            int accessiblePageNumber = pageTable.getAccessiblePhysicalPageNumber();
            Page page = new Page(pageTable.getBits(), pageTable, process, accessiblePageNumber);
            resultList.add(page);
            pageTable.getPhysicalPages()[accessiblePageNumber] = page;
            pageTable.clear(page);
        }
        return resultList;
    }

    public List<Page> countMemory(Process process) {
        Random rnd = new Random();
        double pageCountDiv =  rnd.nextInt(64) + 64 /  pageTable.getSize();
        int pageCountMod =  rnd.nextInt(64) + 64 %  pageTable.getSize();
        int pageCountTotal = (int) pageCountDiv;
        if (pageCountMod > 0) {
            pageCountTotal++;
        }
        if (pageTable.getFreePhysicalPageCount() >= pageCountTotal) {
            List<Page> memoryPages = countPhysicalMemory(process, pageCountTotal);
            return memoryPages;
        } else {
            ArrayList<Page> memoryPages = new ArrayList<Page>();
            int physicalPageCount = pageTable.getFreePhysicalPageCount();
            List<Page> pages = countPhysicalMemory(process, physicalPageCount);
            memoryPages.addAll(pages);
            for (int i = 0; i < pageCountTotal - physicalPageCount; i++) {
                int physicalID = pageTable.unloadPage(pageTable.getUsedPage());
                Page memoryPage = new Page(pageTable.getBits(), pageTable, process, physicalID);
                memoryPages.add(memoryPage);
                pageTable.getPhysicalPages()[physicalID] = memoryPage;
                pageTable.clear(memoryPage);
            }
            return memoryPages;
        }
    }

    public String statusPhysicalMemory() {
        String str = "Состояние физической памяти: \n";
        for (int i = 0; i < pageTable.getPhysicalPages().length; i++) {
            if (pageTable.getPhysicalPages()[i] == null) {
                str += "Страница свободна\n";
                for (int j = 0; j < pageTable.getSize(); j++) {
                    str += pageTable.getPhysicalMemory()[j + pageTable.getSize() * i] + " ";
                    if ((j + 1) % pageTable.getBits() == 0)
                        str += "\n";
                }
            } else
                str += pageTable.getPhysicalPages()[i].print();
        }
        return str;
    }

    public String statusVirtualMemory() {
        String str = "Состояние файла подкачки: \n";
        Set<Page> pages = pageTable.getVirtualPages().keySet();
        Iterator<Page> pageIterator = pages.iterator();
        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            if (!page.isInPhysicalMemory())
                str += page.print();
        }
        str += "\n";
        return str;
    }

    public boolean isPageIsDeleted() {
        return pageIsDeleted;
    }
}
