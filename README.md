# ServiceTranslate

## Compile
1. `mvn clean compile assembly:single`

## Run
1. `java -Dconfig=config.json -Dssl=true -jar target/service-translate-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Build Container
1. `docker build -t se/service-translate .`
2. `docker tag se/service-translate rabbitcarrental.azurecr.io/se/service-translate:latest`
3. `docker login rabbitcarrental.azurecr.io`
4. `docker push rabbitcarrental.azurecr.io/se/service-translate:latest`
5. `docker logout rabbitcarrental.azurecr.io`
6. `docker run -p 8443:8443 se/service-translate`

