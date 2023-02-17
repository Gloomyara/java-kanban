package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.server.KVServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    //Доброго времени суток, уважаемый Артем!
    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        System.out.println(new InetSocketAddress("localhost", 8080));
        KVServer kv = new KVServer();
        kv.start();
    }

}


