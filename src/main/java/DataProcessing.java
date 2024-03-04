
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DataProcessing {


    private List<List<Integer>> ratingsMatrix;

    public DataProcessing(List<String> ratings) {
        this.ratingsMatrix = parseRatings(ratings);
        removeUncooperativeUsers();
    }

    private List<List<Integer>> parseRatings(List<String> ratings) {
        List<List<Integer>> matrix = new ArrayList<>();

        for (String line : ratings) {
            List<Integer> songRatings = new ArrayList<>();
            // Splitting the line by spaces to get individual ratings
            Arrays.stream(line.split("\\s+"))
                  .mapToInt(Integer::parseInt)
                  .forEach(songRatings::add);
            matrix.add(songRatings);
        }

        return matrix;
    }

    public void removeUncooperativeUsers() {
        List<Integer> uncooperativeUsers = new ArrayList<>();
    
        int numUsers = ratingsMatrix.get(0).size();
        for (int userIndex = 0; userIndex < numUsers; userIndex++) {
            if (isUncooperativeUser(userIndex)) {
                uncooperativeUsers.add(userIndex);
            }
        }
    
        for (List<Integer> songRatings : ratingsMatrix) {
            for (int userIndex = uncooperativeUsers.size() - 1; userIndex >= 0; userIndex--) {
                songRatings.remove((int) uncooperativeUsers.get(userIndex));
            }
        }
    }

    private boolean isUncooperativeUser(int userIndex) {
        boolean hasRated = false;
        Integer firstRating = null;
    
        for (List<Integer> songRatings : ratingsMatrix) {
            int rating = songRatings.get(userIndex);
            if (rating != 0) {
                if (firstRating == null) {
                    firstRating = rating;
                } else if (rating != firstRating) {
                    return false;
                }
                hasRated = true;
            }
        }
        return !hasRated || (firstRating != null && hasRated);
    }
    
    public List<List<Integer>> getProcessedRatings() {
        return ratingsMatrix;
    }
}
