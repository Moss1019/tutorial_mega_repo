package com.mosson.trade;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.Definition;
import org.jooq.meta.RoutineDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.postgres.PostgresDatabase;

public class Program {
    public static class NamingStrategy extends DefaultGeneratorStrategy {
        @Override
        public String getJavaClassName(Definition definition, Mode mode) {
            if(definition instanceof TableDefinition) {
                return switch(mode) {
                    case DAO, POJO, RECORD -> super.getJavaClassName(definition, mode);
                    default -> String.format("TBL%s", super.getJavaClassName(definition, mode));
                };
            } else if(definition instanceof RoutineDefinition) {
                return String.format("RTN%s", super.getJavaClassName(definition, mode));
            }
            return super.getJavaClassName(definition, mode);
        }
    }

    static void main() {
        var jdbc = new Jdbc()
                .withDriver(org.postgresql.Driver.class.getName())
                .withUser("postgres")
                .withPassword("secret123!")
                .withUrl("jdbc:postgresql://localhost:5432/trade_db");

        var database = new Database()
                .withInputSchema("public")
                .withIncludes(".*")
                .withName(PostgresDatabase.class.getName());

        var generate = new Generate();

        var directory = String.format("%s/trade-db/src/main/java", System.getProperty("user.dir"));
        var target = new Target()
                .withPackageName("com.mosson.trade.db")
                .withDirectory(directory);
        var strategy = new Strategy()
                .withName(Program.NamingStrategy.class.getName());

        var generator = new Generator()
                .withDatabase(database)
                .withGenerate(generate)
                .withTarget(target)
                .withStrategy(strategy);

        var config = new Configuration()
                .withJdbc(jdbc)
                .withGenerator(generator);

        try {
            GenerationTool.generate(config);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
