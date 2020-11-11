package com.company;

public class Page {

    private final int bits;
    private int physicalPageNumber;
    private long timeLastEdit;
    private long timeLastRead;
    private int pageSize;
    private final PageTable pageTable;
    private final Process process;

    public Page(int bits, PageTable pageTable, Process process, int pageNumber) {
        this.bits = bits;
        this.pageTable = pageTable;
        this.process = process;
        this.physicalPageNumber = pageNumber;
        this.pageSize = PageTable.getSize();
        timeLastRead = System.currentTimeMillis();
        timeLastEdit = System.currentTimeMillis();
    }

    public long getLastAppeal() {
        if (timeLastRead > timeLastEdit)
            return timeLastRead;
        return timeLastEdit;
    }

    public void setBytes(int virtualPosition, byte[] data) {
        timeLastEdit = System.currentTimeMillis();
        pageTable.setBytes(this, virtualPosition, data);
    }

    public boolean isInPhysicalMemory() {
        if (physicalPageNumber > -1)
            return true;
        return false;
    }

    public void delete() {
        pageTable.delete(this);
    }

    public String print() {
        String str = "Страница иденцифицируется номером " + physicalPageNumber + "\n";
        if (isInPhysicalMemory()) {
            str += "Располагается в физической памяти\n";
            for (int i = 0; i < pageSize; i++) {
                str += pageTable.physicalMemory[physicalPageNumber * pageSize + i] + " ";
                if ((i + 1) % bits == 0)
                    str += "\n";
            }
            str += "\n";
        } else {
            str = "Располагается в файле подкачки\n";
            byte[] bytes = pageTable.virtualPages.get(this);
            for (int i = 0; i < pageSize; i++) {
                str += bytes[i] + " ";
                if ((i + 1) % bits == 0)
                    str += "\n";
            }
            str += "\n";
        }
        return str;
    }

    public int getPhysicalPageNumber() {
        return physicalPageNumber;
    }

    public void setPhysicalPageNumber(int number) {
        physicalPageNumber = number;
    }

}
