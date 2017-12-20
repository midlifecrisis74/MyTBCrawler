# MyTBCrawler
myTagebuch.de Export nach Wordpress

## Schritt 1 - Java installieren
Um das Programm bei Euch selbst ausführen zu können, benötigt Ihr leider Java :-(. 
Solltet Ihr noch keine Java-Installation auf Eurem Computer haben, könnt Ihr dies kostenfrei hier herunterladen:

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Die Installationsdatei, die Ihr braucht, hängt von Eurem Computer und dessen Betriebssystem ab. 
Windows-Benutzer mit halbwegs aktuellem System werden sehr wahrscheinlich mit jdk-8u152-windows-x64.exe glücklich.
Sobald der Download fertig ist, müsst Ihr das Installationsprogramm starten und Java installieren.

## Schritt 2 - Java Installation testen
Wenn die Installation geklappt hat, solltet Ihr danach in einer DOS-Eingabeaufforderung folgenden Befehle erfolgreich ausführen können (die Doppeltenanführungszeichen sind wichtig):
```
"C:\Program Files\Java\jdk1.8.0_152\bin\java.exe" -version
```

Als Rückmeldung solltet Ihr einen Text mit Eurer Java-Version ähnlich dieser Meldung sehen:
```
java version "1.8.0_152"
Java(TM) SE Runtime Environment (build 1.8.0_152-b16)
Java HotSpot(TM) 64-Bit Server VM (build 25.152-b16, mixed mode)
```

## Schritt 3 - Mein Programm
Danach könnt Ihr mein Export-Programm von dieser Seite herunterladen:

https://github.com/midlifecrisis74/MyTBCrawler/raw/HEAD/mytb2wxr-0.1.jar

Mein Programm mytb2wxr-0.1.jar speichert Ihr am besten in einem leeren Verzeichnis.

Als ersten Text könntet Ihr z.B. die öffentlichen Einträge vom myTagebuch-Team exportieren:
```
"C:\Program Files\Java\jdk1.8.0_152\bin\java.exe" -jar mytb2wxr-0.1.jar -mytb_autoren_id 10877 -mytb_anzahl 10 -export_datei myTagebuch-Team.xml
```

Wenn mein Programm richtig läuft, solltet Ihr eine Ausgabe in dieser Form sehen:
```
1. 01.01.2018 - Das Ende von myTagebuch
2. 20.12.2017 - Jahresendsorgen
3. 19.12.2017 - Wichtige Infos / Statement aus der Technik
4. 13.12.2017 - Eine Gruppe für euch
5. 20.12.2016 - test
```

Es gibt drei Parameter die Ihr ändern müsst, um Euer Tagebuch exportieren zu können:
- ```-mytb_autoren_id``` Das ist Eure Autoren-ID bei myTagebuch, diese findet Ihr in der URL Eures Tagebuch-Profils
- ```-mytb_anzahl``` Die Anzahl an Tagebucheinträgen die Ihr exportieren wollt
- ```-export_datei``` Der Name der Exportdatei
