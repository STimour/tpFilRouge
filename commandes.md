
### Lancer docker (il faut être dans le bon dossier là où est sauvgardé le compose)
- docker-compose up -d

### test app
- mvn clean test

### lancer api
- mvn clean package -DskipTests

### installation de newman 
- npm install -g newman

### lancemant de newman avec l'api
- java -jar socialapp/target/socialapp-0.0.1-SNAPSHOT.jar & sleep 10 newman run tests/tpFilRouge.postman_collection.json

- newman run tests/****.postman_collection.json
