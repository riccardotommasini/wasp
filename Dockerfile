FROM maven:latest 

WORKDIR /code

ADD ./target/mock-jar-with-dependencies.jar ./app.jar

EXPOSE 8182
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "app.jar"]