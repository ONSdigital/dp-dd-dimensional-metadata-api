FROM onsdigital/java-component

WORKDIR /app/

ADD ./target/dd-dimensional-metadata-api-*.jar .

ENTRYPOINT java -jar ./dd-dimensional-metadata-api-*.jar
