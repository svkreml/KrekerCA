import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class CAserver {
    public static void main(String[] args) throws URISyntaxException, MalformedURLException {
        int port = 8082;

        startServer(port);
    }

    public static void startServer(int port) throws URISyntaxException, MalformedURLException {
        Server server = new Server(port);

        URL url = CAserver.class.getClassLoader().getResource("pages/");

        assert url != null;
        //URI webRootUri = url.toURI();

        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");
       // context.setBaseResource(Resource.newResource(webRootUri));
        context.setWelcomeFiles(new String[]{"index.html"});

        ServletHolder holderPwd = new ServletHolder("default",
                DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "true");
        context.addServlet(holderPwd, "/");


        addServlet(context, "/sendfile", new SaveFile());
        addServlet(context, "/ocsp", new OcspServer());
        addServlet(context, "/tsa.srf", new TsaServer());
        //addServlet(context, "/tsa.srf", new OcspServer());

        server.setHandler(context);

        try {
            server.start();
            server.dump(System.err);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static void addServlet(ServletContextHandler context, String path, HttpServlet httpServlet ) {
        ServletHolder fileUploadServletHolder = new ServletHolder( httpServlet);
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp/1029384756"));
        context.addServlet(fileUploadServletHolder, path);
    }
}