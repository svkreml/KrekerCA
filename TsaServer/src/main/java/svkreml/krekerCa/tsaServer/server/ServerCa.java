package svkreml.krekerCa.tsaServer.server;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.core.CertAndKey;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;
import svkreml.krekerCa.tsaServer.tsaServlet.TsaServer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class ServerCa {
    public static void main(String[] args) throws IOException {
        int port = 8082;

        startServer(port);
    }

    public static void startServer(int port) throws IOException {
        Server server = new Server(port);

        // String cert = "keysTemplates/testTsa2001.der";
        // String pkey = "keysTemplates/testTsa2001.der.pkey";

        String cert = "keysTemplates/testTsa2012.der";
        String pkey = "keysTemplates/testTsa2012.der.pkey";

        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);


        BcInit.init();

        X509Certificate x509Certificate = CertEnveloper.decodeCert(FileManager.read(new File(cert)));
        PrivateKey privateKey = CertEnveloper.decodePrivateKey(new File(pkey));
        CertAndKey tsaCertAndKey = new CertAndKey(privateKey, x509Certificate);


        addServlet(context, "/tsa.srf", new TsaServer(tsaCertAndKey));


        server.setHandler(context);

        try {
            server.start();
            server.dump(System.err);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static void addServlet(ServletContextHandler context, String path, HttpServlet httpServlet) {
        ServletHolder fileUploadServletHolder = new ServletHolder(httpServlet);
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp/1029384756"));
        context.addServlet(fileUploadServletHolder, path);
    }
}
