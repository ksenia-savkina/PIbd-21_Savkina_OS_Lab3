package com.company;

import java.util.*;

public class PageTable {

    public HashMap<Page, byte[]> virtualPages;
    protected byte[] physicalMemory;
    private Page[] physicalPages;
    private final int bits = 8;
    private static final int pageSize = 32;
    private final int fullExtent = 256;

    public PageTable() {
        physicalMemory = new byte[fullExtent];
        physicalPages = new Page[fullExtent/pageSize];
        virtualPages = new HashMap<Page, byte[]>();
    }

    public Page getUsedPage() {
        Page pageLRU = physicalPages[0];
        for (int i = 0; i < physicalPages.length; i++) {
            if (physicalPages[i] != null) {
                if (pageLRU == null || pageLRU.getLastAppeal() > physicalPages[i].getLastAppeal()) {
                    pageLRU = physicalPages[i];
                }
            }
        }
        return pageLRU;
    }

    private boolean physicalPageVirtualPage(Page page) {
        if (virtualPages.containsKey(page)) {
            byte[] bytes = virtualPages.get(page);
            for (int i = 0; i < pageSize; i++) {
                if (bytes[i] != physicalMemory[page.getPhysicalPageNumber() * pageSize + i])
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private int unloadPage(Page page) {
        int physicalMemoryNumber = page.getPhysicalPageNumber();
        if (!virtualPages.containsKey(page)) {
            virtualPages.put(page, new byte[pageSize]);
        }
        physicalPages[physicalMemoryNumber] = null;
        if (!physicalPageVirtualPage(page)) {
            byte[] bytes = virtualPages.get(page);
            for (int i = 0; i < pageSize; i++) {
                bytes[i] = physicalMemory[physicalMemoryNumber * pageSize + i];
            }
            virtualPages.replace(page, bytes);
        }
        page.setPhysicalPageNumber(-1);
        return physicalMemoryNumber;
    }

    private void loadPage(Page page) {
        int accessiblePage = getAccessiblePhysicalPageNumber();
        if (accessiblePage > -1) {
            physicalPages[accessiblePage] = page;
            byte[] bytes = virtualPages.get(page);
            page.setPhysicalPageNumber(accessiblePage);
            for (int i = 0; i < pageSize; i++) {
                physicalMemory[accessiblePage * pageSize + i] = bytes[i];
            }
        }
    }

    private int getFreePhysicalPageCount() {
        int k = 0;
        for (int i = 0; i < physicalPages.length; i++) {
            if (physicalPages[i] == null) {
                k++;
            }
        }
        return k;
    }

    private int getAccessiblePhysicalPageNumber() {
        for (int i = 0; i < physicalPages.length; i++) {
            if (physicalPages[i] == null)
                return i;
        }
        return -1;
    }

    public void setBytes(Page page, int positionV, byte[] dataByte) {
        if (!page.isInPhysicalMemory()) {
            if (getFreePhysicalPageCount() == 0) {
                page = getUsedPage();
                unloadPage(page);
            }
            loadPage(page);
        }
        for (int i = 0; positionV + i < pageSize && i < dataByte.length; i++) {
            int physicalPosition = positionV + i + page.getPhysicalPageNumber() * pageSize;
            physicalMemory[physicalPosition] = dataByte[i];
        }
    }

    private List<Page> countPhysicalMemory(Process process, int pageCount) {
        List<Page> resultList = new ArrayList<Page>();
        for (int i = 0; i < pageCount; i++) {
            int accessiblePageNumber = getAccessiblePhysicalPageNumber();
            Page page = new Page(bits, this, process, accessiblePageNumber);
            resultList.add(page);
            physicalPages[accessiblePageNumber] = page;
            clear(page);
        }
        return resultList;
    }

    private void clear(Page page) {
        if (page.isInPhysicalMemory()) {
            for (int i = 0; i < pageSize; i++) {
                int physicalPosition = i + page.getPhysicalPageNumber() * pageSize;
                physicalMemory[physicalPosition] = 0;
            }
        }
    }

    public List<Page> countMemory(Process process) {
        double pageCountDiv = process.getMemoryNumber() / pageSize;
        int pageCountMod = process.getMemoryNumber() % pageSize;
        int pageCountTotal = (int) pageCountDiv;
        if (pageCountMod > 0) {
            pageCountTotal++;
        }
        if (getFreePhysicalPageCount() >= pageCountTotal) {
            List<Page> memoryPages = countPhysicalMemory(process, pageCountTotal);
            return memoryPages;
        } else {
            ArrayList<Page> memoryPages = new ArrayList<Page>();
            int physicalPageCount = getFreePhysicalPageCount();
            List<Page> pages = countPhysicalMemory(process, physicalPageCount);
            memoryPages.addAll(pages);
            for (int i = 0; i < pageCountTotal - physicalPageCount; i++) {
                int physicalID = unloadPage(getUsedPage());
                Page memoryPage = new Page(bits, this, process, physicalID);
                memoryPages.add(memoryPage);
                physicalPages[physicalID] = memoryPage;
                clear(memoryPage);
            }
            return memoryPages;
        }
    }

    public void delete(Page page) {
        clear(page);
        if (page.isInPhysicalMemory())
            physicalPages[page.getPhysicalPageNumber()] = null;
        else
            virtualPages.remove(page);
    }

    public String statusPhysicalMemory() {
        String str = "Состояние физической памяти: \n";
        for (int i = 0; i < physicalPages.length; i++) {
            if (physicalPages[i] == null) {
                str += "Страница свободна\n";
                for (int j = 0; j < pageSize; j++) {
                    str += physicalMemory[j + pageSize * i] + " ";
                    if ((j + 1) % bits == 0)
                        str += "\n";
                }
            } else
                str += physicalPages[i].print();
        }
        return str;
    }

    public String statusVirtualMemory() {
        String str = "Состояние файла подкачки: \n";
        Set<Page> pages = virtualPages.keySet();
        Iterator<Page> pageIterator = pages.iterator();
        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            if (!page.isInPhysicalMemory())
                str += page.print();
        }
        str += "\n";
        return str;
    }

    public static int getSize() {
        return pageSize;
    }

}
