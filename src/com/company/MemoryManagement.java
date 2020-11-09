package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryManagement {

    private List<Process> processes;
    private PageTable pageTable;
    private int processesNumber;
    private int k;
    private boolean launch;

    public MemoryManagement() {
        Random rnd = new Random();
        processes = new ArrayList<Process>();
        processesNumber = rnd.nextInt(3) + 2;
        pageTable = new PageTable();
        k = 0;
        launch = false;
    }

    public String performance() {
        String str = "";
        for (int i = 0; i < processesNumber; i++) {
            Process process = new Process(pageTable);
            processes.add(process);
            str = "Создание процесса:" + "\n" + process.print() + "\n";
        }
        return str;
    }

    public String continuation() {
        if (processes.size() > 0) {
            if (k < processes.size()) {
                Process curProcess = processes.get(k);
                String str = "Выполнение:\n";
                if (launch) {
                    curProcess.performance();
                    k++;
                }
                launch = !launch;
                str += curProcess.print();
                if (curProcess.isPageDeleted()) {
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
}
