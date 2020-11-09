package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        PageTable pageTable = new PageTable();
        MemoryManagement memoryManagement = new MemoryManagement();
        Process process = new Process(pageTable);
        System.out.println(memoryManagement.performance());
        System.out.println(pageTable.statusPhysicalMemory());
        System.out.println(pageTable.statusVirtualMemory());
        String str = "yes";

        while (str.equals("yes")) {
            System.out.println(memoryManagement.continuation());
            str = resume(str);
            if (str.equals("no")) break;
            System.out.println(pageTable.statusPhysicalMemory());
            str = resume(str);
            if (str.equals("no")) break;
            System.out.println(pageTable.statusVirtualMemory());
            str = resume(str);
        }
        System.out.println("Страницы в виртуальной памяти: " + process.pagesInVirtualMemory());
    }

    private static String resume(String str){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Для продолжения введите yes, для выхода no:");
        return  str = scanner.nextLine();
    }
}
