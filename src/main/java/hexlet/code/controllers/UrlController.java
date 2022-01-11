package hexlet.code.controllers;

import hexlet.code.exeption.CustomException;
import hexlet.code.model.Url;
import hexlet.code.service.UrlService;
import io.javalin.http.Handler;
import java.util.List;

public class UrlController {
    private static final UrlService URL_SERVICE = new UrlService();
    private static final String FLASH_INFO = "Страница успешно добавлена";
    private static final String FLASH_EXIST_INFO = "Страница уже существует";
    private static final String FLASH_CHECK_INFO = "Страница успешно проверена";
    private static final String FLASH_DANGER = "Некорректный URL";

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Url> urlList = URL_SERVICE.getUrlList(page);
        ctx.attribute("urls", urlList);
        ctx.attribute("page", page);
        ctx.render("urls/showListUrls.html");
    };

    public static Handler createUrl = ctx -> {
        String urlName = ctx.formParam("url").trim();

        try {
            URL_SERVICE.save(urlName);
            ctx.sessionAttribute("flashInfo", FLASH_INFO);
            ctx.redirect("/urls");
        } catch (CustomException e) {
            switch (e.getErrorCode()) {
                case "InvalidUrl" -> {
                    ctx.sessionAttribute("flashDanger", FLASH_DANGER);
                    ctx.render("index.html");
                }
                case "DublicateUrl" -> {
                    ctx.sessionAttribute("flashInfo", FLASH_EXIST_INFO);
                    ctx.redirect("/urls");
                }
                default -> ctx.redirect("/urls");
            }
        }
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = URL_SERVICE.getUrl(id);
        ctx.attribute("url", url);
        ctx.attribute("urlChecks", url.getUrlChecks());
        ctx.render("urls/showInfoUrl.html");
    };

    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = URL_SERVICE.getUrl(id);
        try {
            URL_SERVICE.checkUrl(url);
            ctx.sessionAttribute("flashInfo", FLASH_CHECK_INFO);
        } catch (Exception e) {
            ctx.sessionAttribute("flashDanger", e.getMessage());
        }
        ctx.attribute("urlChecks", url.getUrlChecks());
        ctx.attribute("url", url);
        ctx.redirect("/urls/" + id);
    };
}
