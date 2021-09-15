package com.company;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RBTree<String,Object> rbt = new RBTree<>();
        Scanner scanner = new Scanner(System.in);
        while (true){
            String key = scanner.next();
            rbt.insert(key,null);
            TreeOperation.show(rbt.getRoot());
        }
    }
}
