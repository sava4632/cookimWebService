package com.cookim.cookimws;

import com.cookim.cookimws.endpoints.RecipeAccessMethods;
import com.cookim.cookimws.endpoints.UserAccessMethods;
import com.cookim.cookimws.model.Model;
import io.javalin.*;
import io.javalin.community.ssl.SSLPlugin;
import io.javalin.community.ssl.TLSConfig;

/**
 *
 * @author cookimadmin
 */
public class CookimWebService {

    public static void main(String[] args) {
        Model model = new Model();

        //HTTP
        //Javalin app = Javalin.create().start(7070);
        //HTTPS      
        SSLPlugin plugin = new SSLPlugin(conf -> {    
            //local
            conf.pemFromPath("src\\SSL\\cert.pem",
                    "src\\SSL\\key.pem", "cookimadmin");
            //ubuntu
//            conf.pemFromPath("/app/SSL/cert.pem",
//                    "/app/SSL/key.pem", "cookimadmin");
            // additional configuration options
            conf.insecurePort = 7070;
            conf.host = null;      // Host to bind to, by default it will bind to all interfaces.
            conf.insecure = true;    // Toggle the default http (insecure) connector.
            conf.secure = true;   // Toggle the default https (secure) connector.
            conf.http2 = true;        // Toggle HTTP/2 Support
            conf.securePort = 443;   // Port to use on the SSL (secure) connector.
            conf.insecurePort = 7070;   // Port to use on the http (insecure) connector.
            conf.sniHostCheck = false;   // Enable SNI hostname verification.
            conf.tlsConfig = TLSConfig.INTERMEDIATE;      // Set the TLS configuration. (by default it uses Mozilla's intermediate configuration)
        });

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.register(plugin);
        }).start();

        

        RecipeAccessMethods recipeEndpoints = new RecipeAccessMethods(model);
        recipeEndpoints.registerEndpoints(app);
                
        UserAccessMethods userEndpoints = new UserAccessMethods(model);
        userEndpoints.registerEndpoints(app);       
    }
}
