# CrowStorm_Client_JavaFX
JavaFX client for interacting with CrowStorm server and API

## Requirements
Tested on: x64 Ubuntu1~16.04.4

1. JDK 8 (v1.8.0_121) - [Install Instructions](http://tipsonubuntu.com/2016/07/31/install-oracle-java-8-9-ubuntu-16-04-linux-mint-18/)

~~~~
sudo add-apt-repository ppa:webupd8team/java
sudo apt update; sudo apt install oracle-java8-installer
< Agree to Oracle's Binary Licencing>
java -version
java version "1.8.0_121"
Java(TM) SE Runtime Environment (build 1.8.0_121-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.121-b13, mixed mode)
javac -version
javac 1.8.0_121
~~~~

2. Netbeans (v8.2) - [NetBeans 8.2 Java SE Installer for Linux/English](https://netbeans.org/downloads/start.html?platform=linux&lang=en&option=javase)

I had some issues with the netbeans install auto-detecting the JDK home.  I was able to get the installed location of the JDK using these commands:

~~~~
readlink -f $(which java)
/usr/lib/jvm/java-8-oracle/jre/bin/java
~~~~

From this path, the JDK path is /usr/lib/jvm/java-8-oracle/. I used this paired with --javahome when running the install script.

~~~~
chmod +x netbeans-8.2-javase-linux.sh
./netbeans-8.2-javase-linux.sh --javahome /usr/lib/jvm/java-8-oracle/
~~~~

After configuring the JDK, Netbeans identified the JDK at /usr. I had to explicitly select the /usr/lib/jvm/java-8-oracle/ selection.


