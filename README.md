# Virtual Threads vs Completable Futures

Purpose of this exercise is to simulate a scenario of processing high volumes of "requests" 
where executed logic is dominated by IO. Two implementations are benchmarked, one using CompletableFutures and another Virtual Threads (Loom).

- program takes two arguments, time interval between each request and duration under test.
- below results are based on 2ms interval between requests and 1 minute duration.
  - e.g. with 2ms interval we are simulating 500 requests per second

## Run Examples

`java -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.DecisioningCompletableFuture 2 60`

`java --enable-preview -Xss1m -Xmx128m -Xms128m -cp target/vthreads-1.0-SNAPSHOT.jar example.DecisioningVirtualThreads 2 60`


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