# otracing-demo-app Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

Quarkus demo app to show using OpenTracing and Jaeger with Your Own Services/Application

Quarkus guide: [Quarkus - USING OPENTRACING](https://quarkus.io/guides/opentracing)

The application has APIs */hello*, */sayHello/text* and */sayRemote/text*

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell
$ mvn quarkus:dev
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------------< org.example.rbaumgar:otracing-demo-app >-----------------
[INFO] Building otracing-demo-app 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- quarkus-maven-plugin:2.6.3.Final:dev (default-cli) @ otracing-demo-app ---
[INFO] Invoking org.apache.maven.plugins:maven-resources-plugin:2.6:resources) @ otracing-demo-app
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 2 resources
[INFO] Invoking io.quarkus.platform:quarkus-maven-plugin:2.6.3.Final:generate-code) @ otracing-demo-app
[INFO] Invoking org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile) @ otracing-demo-app
[INFO] Nothing to compile - all classes are up to date
[INFO] Invoking org.apache.maven.plugins:maven-resources-plugin:2.6:testResources) @ otracing-demo-app
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /home/rbaumgar/demo/jaeger/otracing-demo-app/src/test/resources
[INFO] Invoking io.quarkus.platform:quarkus-maven-plugin:2.6.3.Final:generate-code-tests) @ otracing-demo-app
[INFO] Invoking org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile) @ otracing-demo-app
[INFO] Nothing to compile - all classes are up to date
Listening for transport dt_socket at address: 5005

--
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2022-02-02 08:37:18,646 INFO  [io.quarkus] (Quarkus Main Thread) otracing-demo-app 1.0-SNAPSHOT on JVM (powered by Quarkus 2.6.3.Final) started in 2.834s. Listening on: http://localhost:8080

2022-02-02 08:37:18,651 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2022-02-02 08:37:18,653 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, jaeger, kubernetes, rest-client, resteasy, smallrye-context-propagation, smallrye-opentracing, vertx]
```

and from an other window

```shell
$ curl localhost:8080/hello
hello
$ curl localhost:8080/sayHello/demo1
hello: demo1
$ curl localhost:8080/sayRemote/demo1
hello: demo1 from http://localhost:8080/
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

# Using Jaeger

When you have already an instance of Jaeger running (see [OpenTracing](OpenTracing.md)) you can expose the Jaeger port to localhost.
Execute in a new window:

```shell
$ oc port-forward deployment/my-jaeger 14268:14268
```

## Packaging and running the application

The application can be packaged using:

```shell script
$ mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
$ mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Build an image with podman

```shell
$ mvn clean package -DskipTests
...
$ podman build -f src/main/docker/Dockerfile.jvm -t quarkus/otracing-demo-app-jvm .
STEP 1/10: FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:1.10
STEP 2/10: ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
--> Using cache eb5bff920152f6e037e3767be734f3dbd8f722f81f0f310dc8de08dc04a72d83
--> eb5bff92015
STEP 3/10: ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
--> Using cache 07b9373050c5e31f3ef6ebd2c2a0df6ff24367c9d82ac381d865eff12826331f
--> 07b9373050c
STEP 4/10: COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
--> 464f5a8acfa
STEP 5/10: COPY --chown=185 target/quarkus-app/*.jar /deployments/
--> c7b4ae67bca
STEP 6/10: COPY --chown=185 target/quarkus-app/app/ /deployments/app/
--> 9d21de2b563
STEP 7/10: COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/
--> a1e48617e87
STEP 8/10: EXPOSE 8080
--> 4a6695dab91
STEP 9/10: USER 185
--> e40390dda76
STEP 10/10: ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]
COMMIT quarkus/otracing-demo-app-jvm
--> ebd8fce0edb
Successfully tagged localhost/quarkus/otracing-demo-app-jvm:latest
ebd8fce0edbb962030522313743f2e1778e585ddea9a2dc319d52585fbb9ef8b
```

You can also use *docker*.

## Creating a native executable

You can create a native executable using: 

```shell script
$ mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 

```shell script
$ mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/otracing-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Build an native image with podman

```shell

$ podman build -f src/main/docker/Dockerfile.native -t quarkus/otracing-demo-app-native .
STEP 1/7: FROM quay.io/quarkus/quarkus-micro-image:1.0
Trying to pull quay.io/quarkus/quarkus-micro-image:1.0...
Getting image source signatures
Copying blob 389845530265 done  
Copying blob 1a7767692280 done  
Copying blob 27f1b256fdda done  
Copying blob fee42203cc3e done  
Copying blob 4f4fb700ef54 done  
Copying config e0692b273c done  
Writing manifest to image destination
Storing signatures
STEP 2/7: WORKDIR /work/
--> 4ff66527168
STEP 3/7: RUN chown 1001 /work     && chmod "g+rwX" /work     && chown 1001:root /work
--> 15ffe5e0c47
STEP 4/7: COPY --chown=1001:root target/*-runner /work/application
--> 3fdad207a21
STEP 5/7: EXPOSE 8080
--> 60e239b0853
STEP 6/7: USER 1001
--> f2688e46b2a
STEP 7/7: CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
COMMIT quarkus/otracing-demo-app-native
--> 0d40133e92f
Successfully tagged localhost/quarkus/otracing-demo-app-native:latest
0d40133e92fa1e6e37dcf8642a472d751187425c27b40b7c032af0d6f5ba2286
```

You can also use *docker*.

## Run the image

```shell
$ podman run -i --rm -p 8080:8080 quarkus/otracing-demo-app-jvm
```

# Push image to registry

You might need to login at first.

```shell
$ podman images localhost/quarkus/otracing-demo-app-jvm
REPOSITORY                               TAG      IMAGE ID       CREATED      SIZE
localhost/quarkus/otelcol-demo-app-jvm   latest   0a68fa7e569f   2 days ago   108 MB
$ podman push `podman images localhost/quarkus/otracing-demo-app-jvm -q` docker://quay.io/rbaumgar/otracing-demo-app-jvm
```

## Related Guides

- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)

