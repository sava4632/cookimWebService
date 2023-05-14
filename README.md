# Install and configure Nginx server on ubuntu
Here we will install and configure a remote Nginz server on our linux machine in which we will use
## Step 1 - Install Nginx
To begin we will have to install Nginx by entering the terminal and executing the following commands:

_$sudo apt update_

_$sudo apt install nginx_

## Step 2 - Set up the firewall
Before testing Nginx, your firewall software must be tuned to allow access to the service.
These are the profiles that Nginx has available:
_Available applications:
   * NginxFull
   * Nginx HTTP
   * Nginx HTTPS
   * OpenSSH_
  
We will execute the "Nginx Full" since the application will require that all the profiles are active, for this we need to execute the command:
_$sudo ufw allow 'Nginx Full'_
We can check if the profile has been activated using the command:
_$sudo ufw status_

## Step 3 - Checking your web server
The Server should already be running, to verify that this is the case we can use the following command:
_$systemctl status nginx_
If everything went well, it should show that the service is running:

Output
● nginx.service - A high performance web server and a reverse proxy server
    Loaded: loaded (/lib/systemd/system/nginx.service; enabled; vendor preset: enabled)
    Active: active (running) since Fri 2020-04-20 16:08:19 UTC; 3 days ago
      Docs: man:nginx(8)
  Main PID: 2369 (nginx)
     Tasks: 2 (limit: 1153)
    Memory: 3.5M
    CGroup: /system.slice/nginx.service
            ├─2369 nginx: master process /usr/sbin/nginx -g daemon on; master_process on;
            └─2380 nginx: worker process
           
We can also check if Nginx is already fully usable by using the following line in a browser:
_http://your_server_ip_

If everything went well, you should see a welcome message from Nginx.

## Step 4 - Create folders for the images
Finally we have to create the folder paths in which the user files will be stored.  
When installing Nginx, the default route for the data we want to obtain from the server is /var/www/html, everything that is inside this route will be accessible.  

# Create service for the API to start automatically
Guide to create a service to run a JAR automatically in the background using Systemd

Copies the JAR file to a suitable location on the remote server, for example, /app/cookimWS-1.0-SNAPSHOT-jar-with-dependencies.jar.

Create a configuration file for the Systemd service. You can name it cookimAPI.service and store it in the /etc/systemd/system/ path.

Open the cookimAPI.service file and add the following content:

_[Unit]
Description=Cookim API service for recipe social network_

_[Service]
User=cookimadmin
ExecStart=/usr/bin/java -jar /app/cookimWS-1.0-SNAPSHOT-jar-with-dependencies.jar
SuccessExitStatus=143_

_[Install]
WantedBy=multi-user.target_

Make sure the path /app/cookimWS-1.0-SNAPSHOT-jar-with-dependencies.jar matches the location of the JAR file you copied.

Save the changes to the cookimAPI.service file.

Reload the Systemd configuration by running the following command:
_sudo systemctl daemon-reload_
This will ensure that Systemd takes account of changes made to the configuration file.

Start the service by running the following command:
_sudo systemctl start cookimAPI_
This will start the service using the settings you have specified.

Verify that the service is running by running the following command:
_sudo systemctl status cookimAPI_
If everything has been configured correctly, you should see a message that the service is running. You can press Q to exit the status view.

To ensure that the service starts automatically every time the server is rebooted, run the following command:
_sudo systemctl enable cookimAPI_
This will enable the service to start automatically at system boot.

Remember to adjust the filenames and paths as appropriate for your setup.

I hope this guide will help you to create the service and run the JAR file in the background using Systemd.

# Service to check the status of the API service
Create the service file cookim-checker.service in /etc/systemd/system/ with the following content:

_[Unit]
Description=Cookim API Checker_

_[Service]
type=oneshot
ExecStart=/bin/bash -c 'systemctl is-active --quiet cookimAPI.service || echo "Cookim API is not running!" | wall'_

_[Install]
WantedBy=multi-user.target_

This service runs the command systemctl is-active --quiet cookimAPI.service which checks if the cookimAPI service is running. If the service is not running, a message is sent using the echo command and displayed on the console with the wall command.

Create the cookim-checker.timer file in /etc/systemd/system/ with the following content:

_[Unit]
Description=Timer to check Cookim API_

_[Timer]
OnUnitActiveSec=5m
OnBootSec=5m_

_[Install]
WantedBy=timers.target_

This timer runs the cookim-checker service every 5 minutes after the system boots and every 5 minutes while the system is running.

Enable the service and the timer by running the following commands:
_sudo systemctl enable cookim-checker.service_
_sudo systemctl enable cookim-checker.timer_

This will configure the services to start automatically at system boot.

Start the timer by running the following command:
_sudo systemctl start cookim-checker.timer_

With these steps, the cookim-checker service will run every 5 minutes to check if the cookimAPI service is running and send a warning message if it is not.





