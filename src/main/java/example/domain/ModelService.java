package example.domain;

public interface ModelService {
  String getModelName();
  Double getModelScore(Transaction transaction);

  default boolean failCondition(Transaction t) {
    return false;
  }
}
