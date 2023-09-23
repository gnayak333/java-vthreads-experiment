package example;

import example.domain.Decisioning;
import example.domain.ModelService;
import example.domain.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DecisioningVirtualThreads {
    public static boolean scoreTransaction(Transaction transaction, List<ModelService> models) throws Exception {
        boolean decision;

        try (var scope = new StructuredTaskScope<>()) {

            // Create 3 tasks for each model service (IO)
            var futures = models.stream()
                    .map(x -> scope.fork(() -> x.getModelScore(transaction)))
                    .toList();

            // Wait for all models to complete
            try {
                scope.join();
            } catch (Exception e) {
                throw new RuntimeException("interrupted");
            }

            // Get results for completed models
            var scores = futures.stream()
                    .peek(x -> {
                        if (x.state() == StructuredTaskScope.Subtask.State.FAILED) {
                            Util.log(x.exception().getMessage());
                            x.exception().printStackTrace();
                        }
                    })
                    .filter(x -> x.state() == StructuredTaskScope.Subtask.State.SUCCESS)
                    .map(StructuredTaskScope.Subtask::get).toList();

            // Calculate decision (no IO)
            decision = Decisioning.decision(transaction, scores);

            // Publish Event (IO)
            Decisioning.publishEvent(transaction, true);
        }
        return decision;
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

        try (var scope = new StructuredTaskScope<Boolean>()) {
            List<StructuredTaskScope.Subtask<Boolean>> fs = new ArrayList<>();
            while (System.currentTimeMillis() - now < timeSeconds * 1000) {
                var f = scope.fork(() ->
                        scoreTransaction(
                                new Transaction("123", 15000, "Amazon", 1000, 50000),
                                List.of(modelA, modelB, modelC))
                );
                fs.add(f);
                Thread.sleep(frequency);
            }
            scope.join();
            var result = fs.stream()
                    .map(StructuredTaskScope.Subtask::get).toList();
            finalCount += result.size();
            System.out.println("FinalCount: " + finalCount);
            System.out.println(System.currentTimeMillis() - now);

            // Sleep for a minute before exiting
            Thread.sleep(60000);
        }
    }
}
