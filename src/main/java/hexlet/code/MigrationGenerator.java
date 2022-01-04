package hexlet.code;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class MigrationGenerator {
    public static void main(String[] args) throws IOException {
        DbMigration dbMigration = DbMigration.create();

        // генерим миграционные скрипты для dev-БД (h2) и для промышленной БД (postgres)
        dbMigration.addPlatform(Platform.H2, "h2");
        dbMigration.addPlatform(Platform.POSTGRES, "postgres");

        // Для перегенерации скриптов удалите xml-файл из src/main/resources/dbmigration/model/
        dbMigration.generateMigration();
    }
}
