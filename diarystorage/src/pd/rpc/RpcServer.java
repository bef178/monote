package pd.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServer {

    private class Stub implements Runnable {

        Socket socket = null;

        public Stub(Socket socket) {
            this.socket = socket;
        }

        private Object reflectionCall(String typeName, String methodName,
                Class<?>[] parameterTypes, Object[] parameters)
                throws ClassNotFoundException, NoSuchMethodException,
                SecurityException, IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, InstantiationException {
            Class<?> type = RpcServer.this.registry.getType(typeName);
            if (type == null) {
                throw new ClassNotFoundException("E: [" + typeName + "] not found");
            }
            Method method = type.getMethod(methodName, parameterTypes);
            return method.invoke(getTypeInstance(type), parameters);
        }

        @Override
        public void run() {
            ObjectInputStream provider = null;
            ObjectOutputStream consumer = null;
            try {
                provider = new ObjectInputStream(socket.getInputStream());
                String typeName = provider.readUTF();
                String methodName = provider.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) provider.readObject();
                Object[] parameters = (Object[]) provider.readObject();

                Object result = reflectionCall(typeName, methodName, parameterTypes, parameters);

                consumer = new ObjectOutputStream(socket.getOutputStream());
                consumer.writeObject(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                if (provider != null) {
                    try {
                        provider.close();
                    } catch (Exception e) {
                        // dummy
                    }
                }
                if (consumer != null) {
                    try {
                        consumer.close();
                    } catch (Exception e) {
                        // dummy
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        // dummy
                    }
                }
            }
        }
    }

    static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private final int port;

    private final ServiceRegistry registry;

    private ServerSocket serverSocket = null;

    private String threadName = RpcServer.class.getSimpleName();

    private Thread managedThread = null;

    private Object locker = new Object();

    public RpcServer(int port) {
        this(port, new ServiceRegistry());
    }

    public RpcServer(int port, ServiceRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    public ServiceRegistry getRegistry() {
        return registry;
    }

    private Object getTypeInstance(Class<?> type)
            throws InstantiationException, IllegalAccessException {
        return type.newInstance(); // XXX where IoC/DI would function
    }

    public void setThreadName(String name) {
        this.threadName = name;
    }

    private ServerSocket setupServerSocket(int port) throws IOException {
        int numRetryAllowed = 2;
        while (true) {
            try {
                return new ServerSocket(port);
            } catch (IOException e) {
                if (--numRetryAllowed == 0) {
                    throw e;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // dummy
                }
            }
        }
    }

    public void start() {
        LOGGER.info("server starting");

        if (managedThread != null) {
            return;
        }

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    RpcServer.this.startInPlace();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
        t.setName(String.format("Thread-%s-%d", threadName, t.getId()));
        t.start();
        managedThread = t;

        synchronized (locker) {
            try {
                locker.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("server started");
    }

    /**
     * should run in managed thread
     */
    private void startInPlace() throws IOException {
        LOGGER.info("server starting in place");

        try {
            serverSocket = setupServerSocket(port);
            LOGGER.info("server socket open at {}", port);
        } finally {
            synchronized (locker) {
                locker.notify();
            }
        }

        ExecutorService executor = Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        try {
            while (true) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketException e) {
                    if (serverSocket.isClosed()) {
                        LOGGER.info("server socket closed");
                        break;
                    }
                    throw e;
                }
                LOGGER.info("server recv connection");
                executor.execute(new Stub(socket));
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if (executor != null) {
                executor.shutdown();
                executor = null;
            }
        }
    }

    /**
     * manually stop
     */
    public void stop() {
        LOGGER.info("server stopping");

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // dummy
            }
        }
        if (managedThread != null) {
            try {
                managedThread.join();
            } catch (InterruptedException e) {
                // dummy
            }
            managedThread = null;
        }
        LOGGER.info("server stopped");
    }
}
