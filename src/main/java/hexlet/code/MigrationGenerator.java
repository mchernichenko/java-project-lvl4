package hexlet.code;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class MigrationGenerator {
    public static void main(String[] args) throws IOException {
        DbMigration dbMigration = DbMigration.create();
        //dbMigration.addPlatform(Platform.H2, "h2");
        dbMigration.addPlatform(Platform.POSTGRES, "postgres");

        // Для перегенерации удалите xml-файл из src/main/resources/dbmigration/model/
        dbMigration.generateMigration();
    }
}
