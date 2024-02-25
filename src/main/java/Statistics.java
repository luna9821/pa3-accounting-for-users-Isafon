import java.util.List;

class Statistics {
    public static double calculateMean(List<Double> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return Double.NaN;
        }

        double sum = 0.0;
        for (Double rating : ratings) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    public static double calculateStandardDeviation(List<Double> ratings, double mean) {
        if (ratings == null || ratings.size() <= 1) {
            return Double.NaN;
        }

        double sum = 0.0;
        for (Double rating : ratings) {
            sum += Math.pow(rating - mean, 2);
        }
        return Math.sqrt(sum / (ratings.size() - 1));
    }
}

