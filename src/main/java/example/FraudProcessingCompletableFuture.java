package example;

import example.domain.FraudProcessing;
import example.domain.ModelService;
import example.domain.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FraudProcessingCompletableFuture {

    public static CompletableFuture<Boolean> scoreTransaction(Transaction transaction, List<ModelService> models, ExecutorService es) {
        return CompletableFuture
                .supplyAsync(() -> {
                    // Create 3 tasks for each model service (IO)
                    var cfs = models.stream().map((m) ->
                            CompletableFuture.supplyAsync(() -> m.getModelScore(transaction), es)
                    ).toList();

                    // Wait for all scores to complete
                    CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]))
                            .exceptionally(e -> {
                                e.printStackTrace();
                                return null;
                            })
                            .join();

                    // Get results for completed models
                    return cfs.stream()
                            .peek(x -> {
                                if (x.state() == Future.State.FAILED) {
                                    Util.log(x.exceptionNow().getMessage());
                                }
                            })
                            .filter(x -> x.state() == Future.State.SUCCESS)
                            .map(CompletableFuture::resultNow)
                            .toList();
                }, es)
                // Calculate decision (no IO)
                .thenApplyAsync(scores ->
                        FraudProcessing.decision(transaction, scores), es)
                // Publish Event (IO)
                .whenCompleteAsync((decision, err) ->
                        FraudProcessing.publishEvent(transaction, decision), es);
    }

    public static void main(String[] args) throws Exception {
        var frequency = Integer.valueOf(args[0]);
        var timeSeconds = Integer.valueOf(args[1]);

        var modelA = new ModelServiceImpl("ModelA");
        var modelB = new ModelServiceImpl("ModelB");
        var modelC = new ModelServiceImpl("ModelC") {
            @Override
            public Double getModelScore(Transaction transaction) {
                throw new RuntimeException("Network error when calling model service: " + getModelName());
            }
        };
        var finalCount = 0;
        var now = System.currentTimeMillis();

        ExecutorService es = Executors.newCachedThreadPool();

        var cfs = new ArrayList<CompletableFuture<Boolean>>();
        while (System.currentTimeMillis() - now < timeSeconds * 1000) {
            var cf = scoreTransaction(
                    new Transaction("123", 15000, "Amazon", 1000, 50000),
                    List.of(modelA, modelB, modelC),
                    es
            );
            cfs.add(cf);
            Thread.sleep(frequency);
        }

        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]))
                .join();
        var result = cfs.stream()
                .map(x -> x.resultNow())
                .toList();
        finalCount += result.size();

        System.out.println("FinalCount: " + finalCount);
        System.out.println(System.currentTimeMillis() - now);

        // Sleep for a minute before exiting
        Thread.sleep(60000);
        es.shutdown();
        es.awaitTermination(60000, TimeUnit.MILLISECONDS);
    }
}
