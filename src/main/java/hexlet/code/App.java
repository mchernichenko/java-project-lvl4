package hexlet.code;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;

import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;

import org.thymeleaf.TemplateEngine;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class App {
    private static final String TEMPLATE_DIR = "/templates/";
    private static final String FILE_ENCODING = "UTF-8";
    private static final String PORT = "5000";

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", PORT);
        return Integer.valueOf(port);
    }

    /** Создание инстанса движка шаблонизатора и добавление к нему диалектов.
     *  Настройка преобразователь шаблонов так, чтобы обрабатывались шаблоны в директории /templates/
     *  и добавление преобразователя шаблонов к движку шаблонизатора
     *  @return - инстанс настроенного шаблонизатора thymeleaf
     */
    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(TEMPLATE_DIR);
        templateResolver.setCharacterEncoding(FILE_ENCODING);
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    /**
     * Добавление роутов.
     * GET  / - стартовая страница для проверки введённого URL (index.html)
     * POST /urls - ввод имени введённого URL с формы (параметр "url").
     * После успешного ввода редирект на showListUrls.html иначе остаёмся на index.html
     * GET  /urls - запрос списка URL  (showListUrls.html).
     * GET  /urls/{id} - запрос инфо по конкретному URL (showInfoUrl.html)
     * POST /urls/{id}/checks - проверка URL с добавлением записи о проверке (после - редирект на showInfoUrl.html)
     * @param app - экземпляр Javalin
     */
    private static void addRoutes(Javalin app) {
        app.get("/", RootController.root);

        app.routes(() -> {
            path("urls", () -> {
                get(UrlController.listUrls);
                post(UrlController.createUrl);
                path("{id}", () -> {
                    get(UrlController.showUrl);
                    path(("checks"), () -> {
                        post(UrlController.checkUrl);
                    });
                });
            });
        });
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();  // Включаем логирование
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine()); // Подключаем настроенный шаблонизатор к фреймворку
        });
        addRoutes(app); // Добавляем маршруты в приложение

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
        String inetIP = socket.getLocalAddress().toString();

        int port = getPort();
        String localIP = InetAddress.getLocalHost().getHostAddress();
        String baseUrl = "App listening on ---> http:/" + inetIP + ":" + port;
        System.out.println(baseUrl);
    }

    public static void main(String[] args) throws IOException {
        showAppURL();
        Javalin app = getApp();
        app.start(getPort());
    }
}
