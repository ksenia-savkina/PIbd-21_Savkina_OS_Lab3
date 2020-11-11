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

    public int unloadPage(Page page) {
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

    public int getFreePhysicalPageCount() {
        int k = 0;
        for (int i = 0; i < physicalPages.length; i++) {
            if (physicalPages[i] == null) {
                k++;
            }
        }
        return k;
    }

    public int getAccessiblePhysicalPageNumber() {
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

    public void clear(Page page) {
        if (page.isInPhysicalMemory()) {
            for (int i = 0; i < pageSize; i++) {
                int physicalPosition = i + page.getPhysicalPageNumber() * pageSize;
                physicalMemory[physicalPosition] = 0;
            }
        }
    }

    public void delete(Page page) {
        clear(page);
        if (page.isInPhysicalMemory())
            physicalPages[page.getPhysicalPageNumber()] = null;
        else
            virtualPages.remove(page);
    }

    public int getBits() {
        return bits;
    }

    public Page[] getPhysicalPages(){
        return physicalPages;
    }

    public byte[] getPhysicalMemory() {
        return physicalMemory;
    }

    public static int getSize() {
        return pageSize;
    }

    public HashMap<Page, byte[]> getVirtualPages() {
        return virtualPages;
    }
}
