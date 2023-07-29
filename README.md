# vthreads

## Run Examples

`java -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.FraudProcessingCompletableFuture 2 60`

`java --enable-preview --add-modules jdk.incubator.concurrent -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.FraudProcessingVirtualThreads 2 60`