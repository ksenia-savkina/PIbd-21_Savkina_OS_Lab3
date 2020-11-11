package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        MemoryManagement memoryManagement = new MemoryManagement();
        Process process = new Process(memoryManagement);
        System.out.println(memoryManagement.performance());
        System.out.println(memoryManagement.statusPhysicalMemory());
        System.out.println(memoryManagement.statusVirtualMemory());
        String str = "yes";

        while (str.equals("yes")) {
            System.out.println(memoryManagement.continuation());
            str = resume();
            if (str.equals("no")) break;
            System.out.println(memoryManagement.statusPhysicalMemory());
            str = resume();
            if (str.equals("no")) break;
            System.out.println(memoryManagement.statusVirtualMemory());
            str = resume();
        }
        System.out.println("Страницы в виртуальной памяти: " + process.pagesInVirtualMemory());
    }

    private static String resume(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Для продолжения введите yes, для выхода no:");
        return scanner.nextLine();
    }
}
