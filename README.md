# ServiceTranslate

## Build and Run 

1. `mvn clean compile assembly:single`
2. `docker build -t se/service-translate .`
3. `docker tag se/service-translate rabbitcarrental.azurecr.io/se/service-translate:latest`
4. `docker login rabbitcarrental.azurecr.io`
5. `docker push rabbitcarrental.azurecr.io/se/service-translate:latest`
6. `docker logout rabbitcarrental.azurecr.io`
7. `docker run -p 8443:8443 se/service-translate`

