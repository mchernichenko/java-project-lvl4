package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты на все обработчики:
 * GET  / - стартовая страница для проверки введённого URL (index.html)
 * POST /urls?url= - ввод имени введённого URL с формы
 * После успешного ввода редирект на showListUrls.html иначе остаёмся на index.html
 * GET  /urls - запрос списка URL  (showListUrls.html).
 * GET  /urls/{id} - запрос инфо по конкретному URL (showInfoUrl.html)
 * POST /urls/{id}/checks - проверка URL с добавлением записи о проверке (после - редирект на showInfoUrl.html)
 */
public final class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static Transaction transaction;
    private static MockWebServer mockServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0); // 0 - запуск на случайном порту
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @AfterAll
    static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    // Тест стартовой страницы
    @Test
    void testRoot() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    // Ввод несуществующего URL, проверка успешного перенаправление запроса и добавления url в БД
    @Test
    void testAddUrl() {
        String enteredUrl = "https://www.example.com/bla-bla-bla";
        String expectedUrl = "https://www.example.com";

        // POST /urls?url=enteredUrl
        HttpResponse<?> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asEmpty();
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo("/urls");

        Url actualUrl = new QUrl()
                .name.equalTo(expectedUrl)
                .findOne();
        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(expectedUrl);
    }

    // Ввод существующего URL, проверка успешного перенаправление запроса и недобавления url в БД
    @Test
    void testAddTheSameUrl() {
        String enteredUrl = "https://www.example.com:777";
        String expectedUrl = "https://www.example.com:777";
        Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asEmpty();

        HttpResponse<?> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asEmpty();
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo("/urls");

        response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        // проверяем наличие flash-сообщения на странице со списком url`ов
        String body = (String) response.getBody();
        assertThat(body).contains("Страница уже существует");

        int actualCountUrl = new QUrl()
                .name.equalTo(expectedUrl)
                .findCount();
        assertThat(actualCountUrl).isEqualTo(1);
    }

    // Ввод инвалидного URL, проверка успешного выполнения запроса и добавления flash-сообщения на текущую страницу
    @Test
    void testAddUrlNegative() {
        String enteredUrl = "invalid-url";
        HttpResponse<?> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);

        // проверяем наличие сообщения о невалидности URL
        String body = (String) response.getBody();
        assertThat(body).contains("Некорректный URL");

        // проверяем, что в БД пользователь не добавился
        Url actualUrl = new QUrl()
                .name.equalTo(enteredUrl)
                .findOne();
        assertThat(actualUrl).isNull();
    }

    // Проверка отображение введённого URL в списке: GET /urls
    @Test
    void testGetListUrls() {
        String enteredUrl = "https://www.example.com/bla-bla-bla";
        String expectedUrl = "https://www.example.com";
        HttpResponse<?> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asEmpty();
        assertThat(response.getStatus()).isEqualTo(302);

        response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        // проверяем наличие flash-сообщения на странице со списком url`ов
        String body = (String) response.getBody();
        assertThat(body).contains(expectedUrl);
    }

    // Проверка отображение введённого URL: GET /urls/{id}
    @Test
    void testGetUrl() {
        String enteredUrl = "https://www.example.com/bla-bla-bla";
        String expectedUrl = "https://www.example.com";
        HttpResponse<String> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", enteredUrl)
                .asEmpty();
        assertThat(response.getStatus()).isEqualTo(302);

        Url url = new QUrl().name.equalTo(expectedUrl).findOne();

        response = Unirest
                .get(baseUrl + "/urls/" + url.getId())
                .asString();
        // проверяем наличие ожидаемого значения URL на странице
        String body = response.getBody();
        assertThat(body).contains(expectedUrl);
    }

    @Test
    void testCheckUrl() throws InterruptedException {

        StringBuilder bodyHtml = new StringBuilder();
        bodyHtml.append("""
            <!DOCTYPE html>
            <html>
               <head>
                  <title>testTitle</title>
                  <meta name="description" content="testDescription" />
               </head>
               <body>
                  <h1>testH1</h1>
               </body>
            </html>
            """);

        String moskBaseUrl = mockServer.url("/").toString();
        mockServer.enqueue(new MockResponse().setBody(bodyHtml.toString()));

        // добавляем URL мок-сервиса для проверки и достаём ид БД его id
        Unirest
                .post(baseUrl + "/urls")
                .field("url", moskBaseUrl)
                .asEmpty();
        Url newUrl = new QUrl()
                .name
                .equalTo(moskBaseUrl.substring(0, moskBaseUrl.length() - 1))
                .findOne();

        Unirest
                .post(baseUrl + "/urls/" + newUrl.getId() + "/checks")
                .asEmpty();

        // переходим на страницу сайта для проверки добавленной записи в список проверок
        HttpResponse<String> getResponse = Unirest
                .get(baseUrl + "/urls/" + newUrl.getId())
                .asString();

        assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK);
        String body = getResponse.getBody();
        //assertThat(body).contains("Страница успешно проверена");
        assertThat(body).contains("testTitle");
        assertThat(body).contains("testDescription");
        assertThat(body).contains("testH1");
    }
}
