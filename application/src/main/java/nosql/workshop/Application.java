package nosql.workshop;

import net.codestory.http.WebServer;

public class Application {

    public static void main(String[] args) {
        new WebServer().configure(routes -> routes.get("/api", "NoSQL Workshop API")).start();
    }

}
