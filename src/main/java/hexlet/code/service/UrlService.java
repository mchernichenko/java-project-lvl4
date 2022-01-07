package hexlet.code.service;

import hexlet.code.exeption.CustomException;
import hexlet.code.exeption.CustomExceptions;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import io.ebean.PagedList;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UrlService {
    private static final int ROWS_PER_PAGE = 10; // количество записей на странице

    /**
     * Логика по поверке и сохранению URL.
     * @param name - URL
     * @throws CustomException
     */
    public void save(String name) throws CustomException {
        String formatUrl;
        try {
            formatUrl = urlFormatter(name);
        } catch (MalformedURLException e) {
            throw CustomExceptions.INVALID_URL;
        }

        boolean isExist = new QUrl()
                .name
                .equalTo(formatUrl)
                .exists();
        if (isExist) {
            throw CustomExceptions.DUBLICATE_URL;
        }

        Url url = new Url(formatUrl);
        url.save();
    }

    /**
     * Получить список URLs в количестве 10 элементов для указанной страницы.
     * @param page - номер страницы
     * @return список URLs
     */
    public List<Url> getUrlList(int page) {
        int offset = (page - 1) * ROWS_PER_PAGE;

        PagedList<Url> pagedArticles = new QUrl()
                .setFirstRow(offset)
                .setMaxRows(ROWS_PER_PAGE)
                .orderBy()
                .id.asc()
                .findPagedList();

        return pagedArticles.getList();
    }

    /**
     * Получить URL по идентификатору.
     * @param id - идентификатор URL
     * @return - URL
     */
    public Url getUrl(long id) {
        return new QUrl()
                .id.equalTo(id)
                .findOne();
    }

    /**
     * Форматирование URL до формата: 'протокол://домен:порт' или 'протокол://домен', если порт не был указан.
     * @param name - полное имя URL
     * @return отформатированный URL
     * @throws MalformedURLException - если переданная строка не является URL`ом
     */
    public static String urlFormatter(String name) throws MalformedURLException {
        URL urlObj = new URL(name);
        int port = urlObj.getPort();
        String protocol = urlObj.getProtocol();
        String host = urlObj.getHost();

        return (port == -1)
                ? String.format("%s://%s", protocol, host)
                : String.format("%s://%s:%d", protocol, host, port);
    }

    public static void checkUrl(Url url) {
        HttpResponse<String> response = Unirest
                .get(url.getName())
                .asString();

        String titleTag = "";       // содержимое тега <title>
        String h1Tag = "";          // содержимое первого тега <h1>
        String descriptionTag = ""; // содержимое первого тега <description>

        int statusCode = response.getStatus();
        String htmlContent = response.getBody();

        Document doc = Jsoup.parse(htmlContent);
        if (doc.title() != null) {
            titleTag = doc.title();
        }
        if (doc.select("meta[name=description]").first() != null) {
            descriptionTag = doc.select("meta[name=description]").first().attr("content");
        }
        if (doc.select("h1").first() != null) {
            h1Tag = doc.select("h1").first().text();
        }
        UrlCheck urlCheck = new UrlCheck(statusCode, titleTag, h1Tag, descriptionTag, url);
        urlCheck.save();
    }
}
