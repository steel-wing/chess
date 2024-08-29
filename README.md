# ♕ Terminal Chess

This is a project in software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

All rules of chess are fully implemented (with the exception of the triple-repetition-stalemate rule. I figured that casual players wouldn't need to worry about that one), and players are able to create, join, and observe games stored in the database.

## How It Looks

Both players can see the board from their own perspective, and can highlight any piece on the board to look at its legal moves. Castling and en pessant are also supported.
As the game progresses, the board keeps track of whose turn it is, whether or not a team is in check or checkmate, and prevents players from accidentally moving a chess piece where official rules do not allow.

![command-line chess](https://github.com/user-attachments/assets/a99bd7ee-de1c-4997-b8d5-df5b390f05c6)
Here is an example of Black's turn, selecting a knight on e4 and being shown all options.

## What It Is

This application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## IntelliJ
The project has been outfitted to be run in IntelliJ, and if SQL is installed on the machine, database persistence can be enabled as well.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar
```
