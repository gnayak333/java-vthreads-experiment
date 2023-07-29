package example;

import example.domain.ModelService;
import example.domain.Transaction;

import java.util.random.RandomGenerator;

public class ModelServiceImpl implements ModelService {
    private final String modelName;
    private final String msg = "There was an error when making request to model service: ";
    public ModelServiceImpl(String modelName) {
        this.modelName = modelName;
    }
    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public Double getModelScore(Transaction transaction) {
        var randomSleep = Math.max(50, Math.abs(RandomGenerator.getDefault().nextInt()) % 150);
        Util.log("ID: " + transaction.id() + "; " + "GetModelScore START. Retrieving model score: " + getModelName() + "; sleep for " + randomSleep);

        try {
          Thread.sleep(randomSleep);
        } catch (Exception e) {
          Util.log("ID: " + transaction.id() + "; " + "GetModelScore Interrupted for model " + getModelName());
          throw new RuntimeException("interrupted");
        }

        if (failCondition(transaction)) {
          Util.log("ID: " + transaction.id() + "; " + "GetModelScore failed for model " + getModelName());
          throw new RuntimeException("model failed");
        }

        var randomScore = RandomGenerator.getDefault().nextDouble();
        Util.log("ID: " + transaction.id() + "; " + "GetModelScore END. Score of value " + randomScore + " retrieved for model: " + getModelName());
        return randomScore;
    }
}
