package hexlet.code.controllers;

import hexlet.code.exeption.CustomException;
import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import hexlet.code.service.UrlService;
import io.javalin.http.Handler;
import java.util.List;

public class UrlController {
    private static final UrlService URL_SERVICE = new UrlService();

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Url> urlList = URL_SERVICE.getUrlList(page);
        ctx.attribute("urls", urlList);
        ctx.attribute("page", page);
        ctx.render("urls/showListUrls.html");
    };

    public static Handler createUrl = ctx -> {
        String urlName = ctx.formParam("url").trim();
        UrlService urlService = new UrlService();

        try {
            URL_SERVICE.save(urlName);
            ctx.sessionAttribute("flashInfo", "Страница успешно добавлена");
            ctx.redirect("/urls");
        } catch (CustomException e) {
            switch (e.getErrorCode()) {
                case "InvalidUrl" -> {
                    ctx.sessionAttribute("flashDanger", "Некорректный URL");
                    ctx.render("index.html");
                }
                case "DublicateUrl" -> {
                    ctx.sessionAttribute("flashInfo", "Страница уже существует");
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
        ctx.render("urls/showInfoUrl.html");
    };

    public static Handler checkUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        ctx.attribute("url", url);
        ctx.render("urls/showInfoUrl.html");
    };
}
