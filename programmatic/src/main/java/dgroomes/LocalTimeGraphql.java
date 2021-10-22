package dgroomes;

import graphql.GraphQL;
import graphql.schema.idl.NaturalEnumValuesProvider;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class LocalTimeGraphql {

    private static final Logger log = LoggerFactory.getLogger(LocalTimeGraphql.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Expected exactly one argument but found {}", args.length);
            System.exit(1);
        }

        var graphqlQuery = args[0];

        var schemaParser = new SchemaParser();
        var typeDefinitionRegistry = schemaParser.parse(new File("schema.graphqls"));

        var runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("localTime", new LocalTimeDataFetcher()))
                .type("TimeZone", builder -> builder.enumValues(new NaturalEnumValuesProvider<>(TimeZone.class)))
                .build();

        var schemaGenerator = new SchemaGenerator();
        var graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        var build = GraphQL.newGraphQL(graphQLSchema).build();
        var executionResult = build.execute(graphqlQuery);

        log.info(executionResult.getData().toString());
    }
}
