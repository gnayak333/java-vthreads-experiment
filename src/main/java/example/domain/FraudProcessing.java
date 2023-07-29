package example.domain;

import example.Util;

import java.util.List;

public interface FraudProcessing {
     String SCORES_NOT_AVAILABLE = "Model scores not available.";
     static boolean decision(Transaction transaction, List<Double> scores) {
        Util.log("ID: " + transaction.id() + "; " + "Decision start");
        boolean decision = true;
        var validScores = scores.stream()
                .filter((x) -> x > 0.0)
                .toList();
        var sum = validScores.stream()
                .reduce((x, y) -> x + y).orElse(0.0);

        var avg = 0.0d;
        if (validScores.size() > 0) {
            avg = sum / validScores.size();
        }

        if (avg > 0.5) {
            decision = false;
        }

        Util.log("ID: " + transaction.id() + "; " + "Decision: " + decision + " using scores: " + scores + "; final score: " + avg);
        return decision;
    }

     static void publishEvent(Transaction transaction, boolean decision) {
        Util.log("ID: " + transaction.id() + "; " + "PublishEvent START");
        var response = new Response(transaction, decision);
        try {
            Thread.sleep(10);
        } catch (Exception e) {}
        Util.log("ID: " + transaction.id() + "; " + "PublishEvent END: " + response);
    }
}
