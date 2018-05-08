

#RUN WITH DOCKER

mvn package
docker build -t mockapp
docker run -it --rm -p 8182:8182 mockapp