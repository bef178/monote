package moo.diarystorage.service;

import java.io.IOException;

import moo.diarystorage.serviceimpl.DiaryStorageServiceImplementation;
import pd.rpc.RpcServer;

public class Main {

    public static final int PORT = 50001;

    public static void main(String[] args) throws IOException, InterruptedException {
        RpcServer server = new RpcServer(PORT);
        server.getRegistry().register(IDiaryStorageService.class,
                DiaryStorageServiceImplementation.class);
        server.getRegistry().freeze();

        server.startInNewThread(null);
    }
}
