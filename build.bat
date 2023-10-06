@echo off
echo building project
call gradlew shadowJar
echo building docker image
call docker build -t docker.io/professorsam/cityquiz_webserver:latest .
echo Success!
set /p choice=Do you want to deploy to k8s (Y/N)?
if /i "%choice%"=="Y" (
    echo remove deployment
    call kubectl delete -f deployment-webserver.yml
    echo loading image
    call minikube image rm docker.io/professorsam/cityquiz_webserver:latest
    call minikube image load docker.io/professorsam/cityquiz_webserver:latest
    echo Deploying
    call kubectl apply -f deployment-webserver.yml
) else (
    echo Done!
)