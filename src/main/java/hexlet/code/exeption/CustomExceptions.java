package hexlet.code.exeption;

public class CustomExceptions {
    public static final CustomException INVALID_URL = new CustomException("InvalidUrl", "Некорректный URL");
    public static final CustomException DUBLICATE_URL = new CustomException("DublicateUrl", "Страница уже существует");
}
