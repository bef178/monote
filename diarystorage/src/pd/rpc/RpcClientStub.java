package pd.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcClientStub {

    @SuppressWarnings("unchecked")
    public static <T> T getRemoteService(String host, int port, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(RpcClientStub.class.getClassLoader(), new Class<?>[] {
                interfaceClass
        }, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] parameters)
                    throws Throwable {
                Socket socket = null;
                ObjectOutputStream consumerStream = null;
                ObjectInputStream providerStream = null;
                try {
                    socket = new Socket(host, port);

                    consumerStream = new ObjectOutputStream(socket.getOutputStream());
                    consumerStream.writeUTF(ServiceRegistry.getTypeName(interfaceClass));
                    consumerStream.writeUTF(method.getName());
                    consumerStream.writeObject(method.getParameterTypes());
                    consumerStream.writeObject(parameters);

                    providerStream = new ObjectInputStream(socket.getInputStream());
                    return providerStream.readObject();
                } finally {
                    if (providerStream != null) {
                        providerStream.close();
                    }
                    if (consumerStream != null) {
                        consumerStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                }
            }
        });
    }
}
