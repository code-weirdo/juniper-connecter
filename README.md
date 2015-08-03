# juniper-connecter

A custom graphical front-end for Juniper SSL VPN, specifically for Ubuntu.
This tool is just a Java Swing based GUI front end to Juniper Network's provided Network Connect tool. It sits nicely in your system tray until you need it :)
I have only tested this on Ubuntu 15.04 x64 connecting to a VPN that uses 2 factor authentication.

# How It Works

Juniper Networks provides a tool for connecting to their SSL VPN networks. The problem lies in that you need a DSID before you can use it. You can get that by loggin into
the VPN's web site. Unfortunately, it's provided to you as a cookie, so it's still not as easy as just copying and pasting it from the website to the terminal.
This tool basically emulates a browser to do the login for you, where it is able to access the DSID. Once it has logged you in, it launches and manages network connect.

I found a few python scripts that do this for you, but they weren't exactly what I was looking for.

## Install Network Connect

1) Download the JAR - it's hosted by your VPN, so log into the site and then go to ```https://<your-vpn-server>/dana-cached/nc/ncLinuxApp.jar```
2) ```mkdir -p ~/.juniper_networks/network_connect```
3) Move ncLinuxApp.jar to the folder you just created.
4) Run ```jar -xf ncLinuxApp.jar``` which will extract the contents of the jar file.
5) Run ```gcc -m32 -Wl,-rpath,`pwd` -o ncui libncui.so``` which will build ```ncui```
6) ```echo | openssl s_client -connect your.vpn.website:443 2>&1 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | openssl x509 -outform der > ssl.crt``` will fetch your vpn's web certificate
7) Make sure that ```ncui```, ```ncsvc''' are executable

## Install Juniper Connecter

You should just be able to do a ```mvn clean install```

## Startup

Coming soon...
