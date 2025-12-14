import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class Neo4jConnector implements AutoCloseable {

    private final Driver driver;

    public Neo4jConnector(String uri, String username, String password) {
        this.driver = GraphDatabase.driver(
                uri,
                AuthTokens.basic(username, password)
        );
    }

    // Close driver safely
    @Override
    public void close() {
        driver.close();
    }

    // Test connection
    public void testConnection() {
        try (Session session = driver.session()) {
            Result result = session.run("RETURN 'Neo4j connection successful' AS msg");
            System.out.println(result.single().get("msg").asString());
        }
    }

    // Example: create relationship
    public void createLivesRelation(String personName, String cityName) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(
                    "MATCH (p:Person {name: $person}), (c:City {name: $city}) " +
                    "CREATE (p)-[:LIVES]->(c)",
                    org.neo4j.driver.Values.parameters(
                        "person", personName,
                        "city", cityName
                    )
                );
                return null;
            });
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "PASSWORD"-->Your password;

        try (Neo4jConnector neo4j = new Neo4jConnector(uri, username, password)) {

            neo4j.testConnection();

            // Example usage
            neo4j.createLivesRelation("Mohsin", "Oran");

            System.out.println("Query executed successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

