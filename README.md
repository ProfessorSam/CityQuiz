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

## Warnung

Dieses Projekt sollte mit vorsicht genutzt werden. Aktuell ist weder das Adminpanel Passwort 
geschützt, noch werden Dateien und Antworten serverseitig validiert!

## Features
- 3 verschiedene Arten von Aufgaben (Normale Antwort, Multiple Choice, Bilder)
- Admin übersicht mit Antworten
- Zeit übersicht für Teilnehmer
- Scalierbarkeit des Backends durch Kubernetes Support
- Unterstüzung von Microsoft Sql Server und MySQL
- Deploybar auf Microsoft Azure mit AKS und MSSQL

## Deployment

Um das Projekt zum Laufen zu bringen, müssen folgende dependencies installiert sein:
- Docker um den Container zu bauen
- Java 17
- Ein Kubernets Cluster
- Kubectl

Zuerst sollte man die Aufgaben für die Ralley in der quests.json in /src/main/resouces auf die 
eigenen begebenheiten anpassen. Als Aufgabentypen stehen "answer"(Einfache Antwort), "multiple choice"
(Multiple Choice) und "picture"(Foto aufnehmen) zur Verfügung. Danach sollte das Projekt gebaut 
werden mit ``gradlew shadowJar`` um eine .jar Datei zu erzeugen. Nun muss ein Docker Image 
darauf gebaut werden mit ``docker build . -t [dein imgae name]`` und anschließend in die 
Registry gepushed werden mit ``docker push [dein image name]``. Zum Testen kann auch mein 
aktuelles Testimage genutzt werden ``docker.io/professorsam/cityquiz_webserver``. Um das ganze 
dann auf Kubernetes zu deployen, muss nun zuerst der Imagename in ``deployment-webserver.yml`` 
geändert werden, sowie die Anzahl der Replicas (Es besteht Support für mehr als eine Instanz), 
sowie die secrets in ``secret-database.yml`` und ``secret-filestorage.yml`` geändert werden 
(Base64 encoded). Nun können die Pods, Services und Secrets deployt werden wie folgt:
```shell
kubectl apply -f secret-database.yml
kubectl apply -f secret-filestorage.yml
kubectl apply -f service-database.yml
kubectl apply -f service-filestorage.yml
kubectl apply -f service-http.yml
kubectl apply -f deployment-db.yml
kubectl apply -f deployment-filestorage.yml
# Warten bis Datenbank und Filestorage online sind
kubectl apply -f deployment-webserver.yml
```

Teilnehmer der Stadtralley können nun unter ``example.com/`` an der Ralley teilnehmen und Admins 
können Antworten und Gruppen im Adminpanel unter ``example.com/admin`` einsehen

## Probleme/Anmerkungen

Bei Problemen oder Anmerkungen bitte ein Issue eröffnen

## Entwickler

Zum lokalen Entwickeln habe ich minikube genutzt. Nach Änderungen am Code kann dieser durch die 
"build.bat" schnell und einfach auf das Cluster übertragen werden.