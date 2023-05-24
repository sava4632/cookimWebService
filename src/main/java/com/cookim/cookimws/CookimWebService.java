package com.cookim.cookimws;

import com.cookim.cookimws.endpoints.RecipeAccessMethods;
import com.cookim.cookimws.endpoints.UserAccessMethods;
import com.cookim.cookimws.model.Model;
import io.javalin.*;
import io.javalin.community.ssl.SSLPlugin;
import io.javalin.community.ssl.TLSConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CookimWebService {

    private static final int HTTP_PORT = 7070; // defines the HTTP port to be used
    private static final int HTTPS_PORT = 443; // defines the HTTPS port to be used

    public static void main(String[] args) {
        Model model = new Model(); // creates an instance of the data model

        Javalin app = createJavalinApp(); // creates an instance of the Javalin app
        RecipeAccessMethods recipeEndpoints = new RecipeAccessMethods(model); // creates an instance of the RecipeAccessMethods
        recipeEndpoints.registerEndpoints(app); // registers the recipe endpoints to the Javalin app

        UserAccessMethods userEndpoints = new UserAccessMethods(model); // creates an instance of the UserAccessMethods
        userEndpoints.registerEndpoints(app); // registers the user endpoints to the Javalin app

    }

    private static Javalin createJavalinApp() {
        SSLPlugin sslPlugin = createSslPlugin(); // creates an instance of the SSLPlugin for HTTPS
        return Javalin.create(config -> config.plugins.register(sslPlugin)).start(HTTP_PORT); // creates and starts the Javalin app with the SSLPlugin      
    }

    private static SSLPlugin createSslPlugin() {
        Properties props = new Properties(); // creates an instance of the Properties class
        InputStream is = CookimWebService.class.getResourceAsStream("/config.properties"); // loads the configuration properties file as an input stream
        try {
            props.load(is); // loads the configuration properties from the input stream
        } catch (IOException e) {
            e.printStackTrace(); // handles any I/O exceptions that may occur
        }
        String certPath = props.getProperty("local.cert"); // gets the certificate file path from the configuration properties
        String keyPath = props.getProperty("local.key"); // gets the private key file path from the configuration properties
        String keyPassword = props.getProperty("pass"); // gets the private key password from the configuration properties

        SSLPlugin sslPlugin = new SSLPlugin(conf -> { // creates an instance of the SSLPlugin with the given configuration
            
            //conf.pemFromPath(certPath, keyPath, keyPassword); // sets the certificate and private key file paths and password
            conf.pemFromPath("/app/SSL/cert.pem", "/app/SSL/key.pem", "cookimadmin"); // sets the certificate and private key file paths and password
            conf.insecurePort = HTTP_PORT; // sets the insecure (HTTP) port to be used
            conf.host = null; // sets the server host name (null means any)
            conf.insecure = true; // sets insecure (HTTP) connections to be allowed
            conf.secure = true; // sets secure (HTTPS) connections to be required
            conf.http2 = true; // sets HTTP/2 support to be enabled
            conf.securePort = HTTPS_PORT; // sets the secure (HTTPS) port to be used
            conf.sniHostCheck = false; // sets SNI host checks to be disabled
            conf.tlsConfig = TLSConfig.INTERMEDIATE; // sets the TLS configuration to be used
        });
        return sslPlugin; // returns the SSLPlugin instance
    }
}
