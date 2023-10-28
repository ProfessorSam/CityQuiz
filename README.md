# CityQuiz

Dies ist das Repository für mein Stadt Ralley Projekt für den Deutsch/Französichen Schüleraustausch

## Motivation/Ziele

Meine Motivation hinter dem Projekt war es zum einen die langweilige Stadt Ralley meiner Schule 
durch etwas neues digitales zu erstzen mit mehr möglichkeiten und zum anderen mit neuen 
Technologien zu arbeiten. Diese sind zum Beispiel:
- Kubernetes
- Gradle
- S3/Minio
- Templating Engine(jte)/Frontend development
- Helm Charts

## Warnung

Dieses Projekt sollte mit vorsicht genutzt werden. Aktuell werden Antworten und Dateien nicht 
Serverseitig validiert

## Features
- 3 verschiedene Arten von Aufgaben (Normale Antwort, Multiple Choice, Bilder)
- Adminübersicht mit Antworten (Token/Passwort gesichert)
- Zeit übersicht für Teilnehmer
- Scalierbarkeit des Backends durch Kubernetes Support
- Unterstüzung von Microsoft Sql Server und MySQL
- Deploybar auf Microsoft Azure mit AKS und MSSQL
- Export aller Fotos

## Deployment

Um das Projekt zum Laufen zu bringen, müssen folgende dependencies installiert sein:
- Docker um den Container zu bauen
- Java 17
- Ein Kubernets Cluster
- Kubectl
- Helm

Zuerst sollte man die Aufgaben für die Ralley in der quests.json in ``/src/main/resouces`` auf die 
eigenen begebenheiten anpassen. Als Aufgabentypen stehen "answer"(Einfache Antwort), "multiple choice"
(Multiple Choice) und "picture"(Foto aufnehmen) zur Verfügung. Danach sollte das Projekt gebaut 
werden mit ``gradlew shadowJar`` um eine .jar Datei zu erzeugen. Nun muss ein Docker Image 
darauf gebaut werden mit ``docker build . -t [dein imgae name]`` und anschließend in die 
Registry gepushed werden mit ``docker push [dein image name]``. Nun muss dieses Image in einer 
values.yml Datei unter image.name und image.tag eingetragen werden. Ist dies erfolgt können 
weitere Einstellungen dort vorgenommen werden. Als Referenz kann die default values.yml in 
cityquiz/values.yml herangezogen werden. Sollte statt einer MySQL Datenbank ein Microsoft SQL 
Server verwendet werden, so kann unter ``mssql.sqlserverjdbc`` ein JDBC-Connection String 
konfiguriert werden.

Um das Projekt nun zu deployen, kann ``helm install [releaseName] -f [deine-values.yml] .
/cityquiz`` genutzt 
werden, 
sofern im root Verzeichnis des Projekts ausgeführt.

Teilnehmer der Stadtralley können nun unter ``example.com/`` an der Ralley teilnehmen und Admins 
können Antworten und Gruppen im Adminpanel unter ``example.com/admin?token=[admin token values.yml webserver.token]`` einsehen. Wenn kein token konfiguriert wurde, ist die Adminübersicht 
für jeden sichtbar!

## Probleme/Anmerkungen

Bei Problemen oder Anmerkungen bitte ein Issue eröffnen

## Entwickler

Zum lokalen Entwickeln unter Windows kann Docker Desktop mit dem build-in K8s Cluster genutzt 
werden.