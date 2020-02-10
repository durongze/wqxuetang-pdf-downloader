package online.githuboy.wqxuetang.pdfd;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.wqxuetang.pdfd.ui.WebContainer;
import online.githuboy.wqxuetang.pdfd.utils.Cli;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class Main extends Application {


    public static void main(String[] args) throws IOException {
        Cli cli = new Cli();
        CommandLine commandLine = cli.get(args);
        if (null == commandLine) System.exit(1);
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/nvc", new RequestHandler());
        server.start();
        log.info("Start internal http server success,Listen on port:{}", 8081);
        initFx();
        launch(args);
    }

    private synchronized static void initFx() {
        // Note that calling PlatformImpl.startup more than once is OK
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                // No need to do anything here
            }
        });
    }

    @Override
    public void start(final Stage stage) {
        WebContainer container = new WebContainer();
        VBox stackPane = container.getStackPane();
        Scene scene = new Scene(stackPane, 600, 400);
        scene.setRoot(stackPane);
        stage.setScene(scene);
        stage.show();
        container.initEngine();
        stage.setOnCloseRequest(event -> {
            System.exit(1);
        });
    }

    static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream in = ResourceUtil.getStream("page/fakepage.html");
            byte[] bytes = IoUtil.readBytes(in);
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

}
