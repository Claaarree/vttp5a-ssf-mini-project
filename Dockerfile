FROM eclipse-temurin:23-jdk AS compiler
# or eclipse-temurin:23-noble
# can be AS builder also

ARG COMPILE_DIR=/code_folder

WORKDIR ${COMPILE_DIR}

COPY src src
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

# Stage 2
FROM eclipse-temurin:23-jdk
# better to use eclipse-temurin:23-jre-jdk

ARG DEPLOY_DIR=/app

WORKDIR ${DEPLOY_DIR}

COPY --from=compiler /code_folder/target/vttp5a-ssf-mini-project-0.0.1-SNAPSHOT.jar food_Dairy.jar
# can copy local file to read here
# COPY /app/filepath/examplefile.txt .

# line below is for healthcheck, so that the system can recognize curl command
# RUN apt update && apt install -y curl

ENV SERVER_PORT=3000

EXPOSE ${SERVER_PORT}

# start period is start health check after 5s
# HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
#     CMD curl http://localhost:${SERVER_PORT}/health || exit 1
# can change the localhost to external api link if want to check the "connection" there

ENTRYPOINT [ "java", "-jar", "food_Dairy.jar" ]