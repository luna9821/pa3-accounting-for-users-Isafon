
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

        // Assuming ratingsMatrix is not null and properly initialized
        int numUsers = ratingsMatrix.get(0).size();
        for (int userIndex = 0; userIndex < numUsers; userIndex++) {
            boolean isUncooperative = checkIfUserIsUncooperative(userIndex);
            if (isUncooperative) {
                uncooperativeUsers.add(userIndex);
            }
        }

        // Removing uncooperative users
        for (List<Integer> songRatings : ratingsMatrix) {
            uncooperativeUsers.forEach(userIndex -> songRatings.set(userIndex, null));
        }
        ratingsMatrix.forEach(songRatings -> songRatings.removeIf(rating -> rating == null));
    }

    private boolean checkIfUserIsUncooperative(int userIndex) {
        Integer firstRating = null;
        boolean ratedAnySong = false;

        for (List<Integer> songRatings : ratingsMatrix) {
            Integer rating = songRatings.get(userIndex);
            if (rating != 0) {
                ratedAnySong = true;
                if (firstRating == null) {
                    firstRating = rating;
                } else if (!firstRating.equals(rating)) {
                    return false; 
                }
            }
        }

        return !ratedAnySong || firstRating != null; 
    }

    public List<List<Integer>> getProcessedRatings() {
        return ratingsMatrix;
    }
}



