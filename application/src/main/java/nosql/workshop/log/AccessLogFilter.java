package nosql.workshop.log;

import net.codestory.http.Context;
import net.codestory.http.Request;
import net.codestory.http.filters.Filter;
import net.codestory.http.filters.PayloadSupplier;
import net.codestory.http.logs.Logs;
import net.codestory.http.payload.Payload;
import net.codestory.http.security.User;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AccessLogFilter implements Filter {

    @Override
    public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
        Request request = context.request();
        Payload payload = nextFilter.get();
        User user = context.currentUser();

        // Exemple de log : 127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
        String ip = request.clientAddress().getAddress().getHostAddress();
        String remoteUser = user == null ? "-" : user.name();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss:' 'Z"));
        String method = request.method();
        String protocol = "HTTP/1.1"; // FIXME pour l'instant c'est impossible de récupérer le protocole/version depuis la requête
        int statusCode = payload.code();
        String paylodSize = "-"; // on ne calcul pas la taille de la réponse (pour des raisons de perf)
        String accessLog = ip + " - " + remoteUser + " [" + timestamp + "] \"" + method + " " + uri + " " + protocol + "\" " + statusCode + " " + paylodSize;
        Logs.uri(accessLog);

        return payload;
    }
}
