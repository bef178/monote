package pd.network;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends SocketAcceptor {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private ExecutorService executor;

    private final int numThreads;

    public Server(int port) {
        this(port, 0);
    }

    public Server(int port, int numThreads) {
        super(port);
        if (numThreads <= 0) {
            numThreads = Runtime.getRuntime().availableProcessors() + 1;
        }
        this.numThreads = numThreads;
    }

    @Override
    protected final void onSocket(Socket socket) {

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    onSocketInWorkerThread(socket);
                } finally {
                    closeSocket(socket);
                }
            }
        });
    }

    /**
     * unnecessary to close socket<br/>
     * override me<br/>
     */
    protected void onSocketInWorkerThread(Socket socket) {
        LOGGER.info("socket handled in worker thread");
    }

    @Override
    public void start(Object notifier) throws IOException {
        if (executor != null && !executor.isShutdown()) {
            LOGGER.error("executor already running");
            return;
        }

        executor = Executors.newFixedThreadPool(numThreads);
        LOGGER.info("executor created with {} threads", numThreads);

        try {
            super.start(notifier);
        } finally {
            if (executor != null) {
                executor.shutdown();
                executor = null;
                LOGGER.info("executor shutdown");
            }
        }
    }
}
