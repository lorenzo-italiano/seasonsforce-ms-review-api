mvn clean install

mv target/seasonsforce-ms-review-api-1.0-SNAPSHOT.jar api-image/seasonsforce-ms-review-api-1.0-SNAPSHOT.jar

cd api-image

docker build -t review-api .

cd ../mongo-image

docker build -t review-db .
