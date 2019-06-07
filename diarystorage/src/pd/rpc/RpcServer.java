package pd.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pd.network.Server;

public class RpcServer extends Server {

    static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private final ServiceRegistry registry;

    public RpcServer(int port) {
        this(port, new ServiceRegistry());
    }

    public RpcServer(int port, ServiceRegistry registry) {
        super(port);
        this.registry = registry;
    }

    public ServiceRegistry getRegistry() {
        return registry;
    }

    private Object getTypeInstance(Class<?> type)
            throws InstantiationException, IllegalAccessException {
        return type.newInstance(); // XXX where IoC/DI would function
    }

    @Override
    protected void onSocketInWorkerThread(Socket socket) {
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
}
