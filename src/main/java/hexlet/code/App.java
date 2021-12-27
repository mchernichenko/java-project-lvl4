package hexlet.code;

// Импортируем зависимости, необходимые для работы приложения
import hexlet.code.controllers.WelcomeController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import org.thymeleaf.TemplateEngine;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.valueOf(port);
    }

    // Javalin поддерживает работу с шаблонизатором thymeleaf
    private static TemplateEngine getTemplateEngine() {
        // Создаём инстанс движка шаблонизатора
        TemplateEngine templateEngine = new TemplateEngine();
        // Добавляем к нему диалекты
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        // Настраиваем преобразователь шаблонов, так, чтобы обрабатывались
        // шаблоны в директории /templates/
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        // Добавляем преобразователь шаблонов к движку шаблонизатора
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    // Метод добавляет маршруты в переданное приложение
    private static void addRoutes(Javalin app) {
        // Для GET-запроса на маршрут / будет выполняться
        // обработчик welcome в контроллере WelcomeController
        app.get("/", WelcomeController.welcome); // ctx -> {ctx.render("index.html");}
    }

    public static Javalin getApp() {

        // Создаём приложение
        Javalin app = Javalin.create(config -> {
            // Включаем логгирование
            config.enableDevLogging();
            // Подключаем настроенный шаблонизатор к фреймворку
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        // Добавляем маршруты в приложение
        addRoutes(app);

        // Обработчик before запускается перед каждым запросом
        // Устанавливаем атрибут ctx для запросов
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }

    private static void showAppURL() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80)); // получить внешний ip-адрес
        String InetIP = socket.getLocalAddress().toString();

        int port = getPort();
        String LocalIP = InetAddress.getLocalHost().getHostAddress();
        String baseUrl = "App listening on ---> http:/" + InetIP + ":" + port;
        System.out.println(baseUrl);
    }

    public static void main(String[] args) throws IOException {
        showAppURL();
        Javalin app = getApp();
        app.start(getPort());
    }
}
