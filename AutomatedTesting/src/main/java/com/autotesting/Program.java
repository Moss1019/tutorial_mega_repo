package com.autotesting;

import com.autotesting.calculator.ProfitLossCalculator;
import com.autotesting.model.Trade;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.mariadb.MariaDBDatabase;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class Program {
    static void main() {
        // run this to generate JOOQ classes
//        generateJooqClasses();
        var trades = new LinkedList<Trade>();
        trades.add(
                new Trade("test-1",
                        "instr-1",
                        1.0,
                        LocalDateTime.now(),
                        LocalDateTime.now(), 1.1,
                        false,
                        "account-1")
        );
        trades.add(
                new Trade("test-2",
                        "instr-1",
                        1.0,
                        LocalDateTime.now(),
                        LocalDateTime.now(), 1.1,
                        false,
                        "account-1")
        );
        var calculator = new ProfitLossCalculator();
        var totalFromTrades = calculator.calculate(trades);
        System.out.println(totalFromTrades);
    }

    private static void generateJooqClasses() {

        var jdbc = new Jdbc()
                .withUrl("jdbc:mariadb://localhost:3306/test_db")
                .withUser("root")
                .withPassword("secret123!")
                .withDriver(org.mariadb.jdbc.Driver.class.getName());

        var generate = new Generate();

        var target = new Target()
                .withDirectory(String.format("%s/src/main/java", System.getProperty("user.dir")))
                .withPackageName("com.autotesting.db");

        var database = new Database()
                .withIncludes(".*")
                .withName(MariaDBDatabase.class.getName())
                .withInputSchema("test_db");

        var strategy = new Strategy()
                .withName(NamingStrategy.class.getName());

        var generator = new Generator()
                .withGenerate(generate)
                .withTarget(target)
                .withDatabase(database)
                .withStrategy(strategy);

        var config = new Configuration()
                .withJdbc(jdbc)
                .withGenerator(generator);

        try {
            GenerationTool.generate(config);
        } catch (Exception ex) {
            System.out.println("Error while generating classes " + ex.getMessage());
        }

    }

    public static class NamingStrategy extends DefaultGeneratorStrategy {
        @Override
        public String getJavaClassName(Definition definition, Mode mode) {
            if(definition instanceof TableDefinition) {
                if(mode == Mode.DEFAULT) {
                    return String.format("TBL%s", super.getJavaClassName(definition, mode));
                }
            }
            return super.getJavaClassName(definition, mode);
        }
    }
}

