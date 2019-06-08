package pd.network.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pd.network.Server;

public class EchoServer extends Server {

    static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        EchoServer server = new EchoServer();
        server.startInNewThread(null).join();
    }

    public EchoServer() {
        super(8881);
    }

    @Override
    protected final void onSocketInWorkerThread(Socket socket) {

        RequestContext context = RequestContext.build(socket);
        if (context == null) {
            return;
        }

        try {
            onRequest(context);
        } catch (Exception e) {
            LOGGER.error("exception when handle request: {}", e.getMessage());
        }
    }

    protected void onRequest(RequestContext context) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.reqStream));
        PrintWriter writer = new PrintWriter(context.ackStream, true);

        StringBuilder ackBody = new StringBuilder();
        ackBody.append("<html>");
        ackBody.append("<head><title>Echo server</title></head>");
        ackBody.append("<body>");
        ackBody.append("<h1>Welcome to Echo server</h1>");
        ackBody.append("<div style=\"font-family: monospace;\">");
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            ackBody.append(line).append("<br/>");
        }
        ackBody.append("&lt;EOF&gt;").append("<br/>");
        ackBody.append("</div>");
        ackBody.append("</body>");
        ackBody.append("</html>");

        StringBuilder ack = new StringBuilder();
        ack.append("HTTP/1.0 200").append("\r\n");
        ack.append("Content-Type: text/html").append("\r\n");
        ack.append("Content-Length: ").append(ackBody.length()).append("\r\n");
        ack.append("\r\n");

        writer.print(ack.toString());
        writer.print(ackBody.toString());
        writer.flush();

        writer.close();
        reader.close();
    }
}
