WordNotify
==========

Just use libNotify to show words and there meaning from cambridge Dictonery


1. You need to get the java-gnome on your system
2. start the notification deamon of libnotify if not started
3. Run this java program

Command to Run:

nohup java -cp /home/gaurav/java/target/api-client-test-0.0.1-SNAPSHOT.jar:/home/gaurav/java/target/dependency/*:/usr/share/java/gtk.jar DictNotification  https://dictionary.cambridge.org <apiKey from cambridge> <seconds to wait for meaning after word> <minutes to wait for next words> &


Api key you need to request from cambridge, it's easy to get.


