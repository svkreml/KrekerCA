package svkreml.krekerCa.ocspServer;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CRLHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import sun.security.x509.X509CRLImpl;
import svkreml.krekerCa.core.BcInit;
import svkreml.krekerCa.fileManagement.CertEnveloper;
import svkreml.krekerCa.fileManagement.FileManager;
import svkreml.krekerCa.fileManagement.Json;
import svkreml.krekerCa.ocspServer.config.OcspConfig;
import svkreml.krekerCa.ocspServer.config.ServletInstance;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;

@Slf4j
public class OcspServer {

    private static OcspConfig ocspConfig;

    public static void main(String[] args) {
        try {
            log.info("****************************************************************");
            log.info("***----------------------------------------------------------***");
            log.info("****************************************************************");
            log.info("Program ocspServer started");
            log.info("**********************************");
            BcInit.init();
            log.info("BC crypto provider loaded");
            File file = null;
            try {
                file = new File("ocspConfig.json");
                ocspConfig = (OcspConfig) Json.readValue(OcspConfig.class, file);
            } catch (Exception e) {
                throw new IOException("Problems with reading config: " + Objects.requireNonNull(file).getAbsolutePath(), e);
            }


            startServer(ocspConfig.getServerPort());

        } catch (Exception e) {
            log.error("Critical error", e);
            System.out.println("Critical error");
            e.printStackTrace();
            System.exit(1);
        }
    }


    private static void startServer(int port) throws Exception {
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");


        for (ServletInstance servletInstance : ocspConfig.getServletInstances()) {
            X509Certificate caCert = CertEnveloper.decodeCert(FileManager.read(new File(servletInstance.getCaCertPath())));
            X509Certificate ocspSigningCert = CertEnveloper.decodeCert(FileManager.read(
                    new File(servletInstance.getOcspSingingCertPath())
            ));
            PrivateKey privateKey = CertEnveloper.decodePrivateKey(
                    new File(servletInstance.getOcspSingingPkeyPath())
            );

            OcspServlet ocspServlet = new OcspServlet(
                    Objects.requireNonNull(caCert),
                    Objects.requireNonNull(ocspSigningCert),
                    privateKey);

            addServlet(context, servletInstance.getServerPath(), ocspServlet);

            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        log.info("Reloading crl");
                        if (servletInstance.getCrlPath() != null && !servletInstance.getCrlPath().isEmpty()) {
                            X509CRLHolder x509CRLHolder = new X509CRLHolder(
                                    FileManager.read(new File(servletInstance.getCrlPath())));

                            ocspServlet.setX509CRL(new X509CRLImpl(x509CRLHolder.getEncoded()));
                            Thread.sleep(10000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            server.setHandler(context);
        }

        server.start();
        server.dump(System.err);
    }

    private static void addServlet(ServletContextHandler context, String path, HttpServlet httpServlet) {
        ServletHolder servletHolder = new ServletHolder(httpServlet);
        servletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp/1029384756"));
        context.addServlet(servletHolder, path);
    }
}
