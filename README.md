# vthreads

## Run Examples

`java -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.DecisioningCompletableFuture 2 60`

`java --enable-preview --add-modules jdk.incubator.concurrent -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.DecisioningVirtualThreads 2 60`


# Results for local test

>System: 
> 
> MacBook Pro, 
> Quad-Core Intel Core i7, 
> 2.2 GHz, 
> 4 cores,
> L2 256KB,
> L3 6MB,
> RAM: 16 GB



| Statistic        | CompletableFutures | Virtual Threads |
|------------------|--------------------|-----------------|
| Thread count     | 194                | 32              |
| Context switches | 474,675            | 847,098         |
| CPU time         | 19.16s             | 20.21           |
| Unix calls       | 1,381,795          | 1,981,173       |