
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class CS_214_Project {
    public static void main(String[] args) throws IOException {
            if (args.length != 3) {
                System.err.println("Error: Incorrect number of arguments. Please provide input file for song titles, input file for ratings, and output file.");
                System.exit(1); // Exit with error code 1 for incorrect arguments
            }
            
            String songTitlesFile = args[0];
            String ratingsFile = args[1];
            String outputFile = args[2];
    
            List<String> songNames = readSongNames(songTitlesFile);
            if (songNames == null) {
                System.exit(2); // Exit with error code 2 for song names reading error
            }
    
            List<List<Integer>> ratings = readRatings(ratingsFile, songNames.size());
            if (ratings == null) {
                System.exit(3); // Exit with error code 3 for ratings reading error
            }
    
            List<String> outputLines = processRatings(songNames, ratings);
            writeOutput(outputFile, outputLines);
    
            System.exit(0); // Successful completion
        }

        public static List<List<Integer>> removeUncooperativeUsers(List<List<Integer>> ratings) {
            List<List<Integer>> filteredRatings = new ArrayList<>();
            for (List<Integer> userRatings : ratings) {
                boolean hasDistinctRatings = userRatings.stream().distinct().count() > 1;
                boolean allZeros = userRatings.stream().allMatch(rating -> rating == 0);
                if (hasDistinctRatings && !allZeros) {
                    filteredRatings.add(userRatings);
                }
            }
            return filteredRatings;
        }

        public static List<List<Integer>> processRatingsAndRemoveUncooperativeUsers(List<List<Integer>> songRatings) {
        // Transpose ratings to be user-centric
        List<List<Integer>> userRatings = transposeRatings(songRatings);

        // Identify uncooperative users
        Set<Integer> uncooperativeUsers = new HashSet<>();
        for (int i = 0; i < userRatings.size(); i++) {
            if (isUncooperativeUser(userRatings.get(i))) {
                uncooperativeUsers.add(i);
            }
        }

        // Remove ratings of uncooperative users
        for (List<Integer> ratings : songRatings) {
            for (int i = uncooperativeUsers.size() - 1; i >= 0; i--) {
                ratings.remove((int) uncooperativeUsers.toArray()[i]);
            }
        }

        return songRatings;
    }

    private static List<List<Integer>> transposeRatings(List<List<Integer>> songRatings) {
        List<List<Integer>> transposed = new ArrayList<>();
        if (!songRatings.isEmpty()) {
            int numUsers = songRatings.get(0).size();
            for (int i = 0; i < numUsers; i++) {
                List<Integer> userRating = new ArrayList<>();
                for (List<Integer> ratings : songRatings) {
                    userRating.add(ratings.get(i));
                }
                transposed.add(userRating);
            }
        }
        return transposed;
    }

    private static boolean isUncooperativeUser(List<Integer> userRatings) {
        boolean allZeros = true;
        Integer firstRating = null;
        for (int rating : userRatings) {
            if (rating != 0) {
                allZeros = false;
                if (firstRating == null) {
                    firstRating = rating;
                } else if (!firstRating.equals(rating)) {
                    return false;
                }
            }
        }
        return allZeros || firstRating != null;
    }


        public static List<String> readSongs(String filename) throws IOException {
        List<String> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                songs.add(line.trim());
            }
        }
        return songs;
    }

    public static List<String> readSongNames(String filename) throws IOException {
        List<String> songNames = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    throw new IOException("Error: Empty line found in song names file.");
                }
                songNames.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Error: File not found: " + e.getMessage());
        }
        return songNames;
    }
    

    public static List<List<Integer>> readRatings(String filename, int expectedSize) {
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("Error: File not found - " + filename);
            return null;
        }
    
        List<List<Integer>> ratings = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length != expectedSize) {
                    System.err.println("Error: Number of Ratings Mismatch");
                    return null;
                }
                List<Integer> songRatings = new ArrayList<>();
                for (String part : parts) {
                    try {
                        int rating = Integer.parseInt(part);
                        if (rating < 0 || rating > 5) {
                            throw new IllegalArgumentException("Error: Invalid rating value: " + rating);
                        }
                        songRatings.add(rating);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Error: Invalid rating format: " + part);
                        }
                }
                ratings.add(songRatings);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
        return ratings;
    }
    
    public static List<List<Integer>> readRatings(String filename) throws IOException {
        List<List<Integer>> ratings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<Integer> userRatings = new ArrayList<>();
                for (String rating : line.split("\\s+")) {  
                    userRatings.add(Integer.parseInt(rating));
                }
                ratings.add(userRatings);
            }
        }
        return ratings;
    }

    public static List<String> processRatings(List<String> songNames, List<List<Integer>> ratings) {
        // Remove uncooperative users
        List<List<Integer>> filteredRatings = removeUncooperativeUsers(ratings);

        // Calculate mean and standard deviation for each user
        List<Double> userMeans = calculateUserMeans(filteredRatings);
        List<Double> userDeviations = calculateUserDeviations(filteredRatings, userMeans);

        // Normalizing these ratings
        List<List<Double>> normalizedRatings = normalizeRatings(filteredRatings, userMeans, userDeviations);

        // Calculating mean and standard deviation for each song
        List<Double> songMeans = calculateSongMeans(normalizedRatings);
        List<Double> songDeviations = calculateSongDeviations(normalizedRatings, songMeans);

        // Prepare output lines
        List<String> outputLines = new ArrayList<>();
        for (int i = 0; i < songNames.size(); i++) {
            String songName = songNames.get(i);
            double mean = songMeans.get(i);
            double deviation = songDeviations.get(i);
            outputLines.add(songName + " " + (Double.isNaN(mean) ? "UNDEFINED" : mean) + " " + (Double.isNaN(deviation) ? "UNDEFINED" : deviation));
        }

        return outputLines;
    }


    public static List<Double> calculateUserMeans(List<List<Integer>> ratings) {
        List<Double> userMeans = new ArrayList<>();
        for (List<Integer> userRatings : ratings) {
            double sum = 0;
            int count = 0;
            for (Integer rating : userRatings) {
                if (rating != 0) {
                    sum += rating;
                    count++;
                }
            }
            userMeans.add(count > 0 ? sum / count : Double.NaN);
        }
        return userMeans;
    }
    

    public static List<Double> calculateUserDeviations(List<List<Integer>> ratings, List<Double> userMeans) {
        List<Double> userDeviations = new ArrayList<>();
        for (int i = 0; i < ratings.size(); i++) {
            List<Integer> userRatings = ratings.get(i);
            double mean = userMeans.get(i);
            double deviation = Math.sqrt(userRatings.stream().mapToDouble(rating -> {
                if (rating != 0) {
                    return Math.pow(rating - mean, 2);
                } else {
                    return 0;
                }
            }).average().orElse(Double.NaN));
            userDeviations.add(deviation);
        }
        return userDeviations;
    }

public static List<List<Double>> normalizeRatings(List<List<Integer>> ratings) {
    if (ratings == null) {
        return Collections.emptyList(); // Return empty list if input is null
    }

    List<List<Double>> normalizedRatings = new ArrayList<>();
    for (List<Integer> userRatings : ratings) {
        if (userRatings == null || userRatings.isEmpty()) {
            normalizedRatings.add(Collections.emptyList()); // Add empty list for null or empty user ratings
            continue;
        }

        double mean = calculateMean(userRatings);
        double stdDev = calculateStandardDeviation(userRatings, mean);
        List<Double> normalizedUserRatings = new ArrayList<>();

        for (Integer rating : userRatings) {
            if (rating == 0) {
                normalizedUserRatings.add(0.0);
            } else {
                normalizedUserRatings.add((rating - mean) / stdDev);
            }
        }
        normalizedRatings.add(normalizedUserRatings);
    }
    return normalizedRatings;
}


    private static double calculateMean(List<Integer> ratings) {
        int sum = 0;
        int count = 0;
        for (Integer rating : ratings) {
            if (rating != 0) {
                sum += rating;
                count++;
            }
        }
        return count == 0 ? 0 : (double) sum / count;
    }

    private static double calculateStandardDeviation(List<Integer> ratings, double mean) {
        double sum = 0;
        int count = 0;
        for (Integer rating : ratings) {
            if (rating != 0) {
                sum += Math.pow(rating - mean, 2);
                count++;
            }
        }
        return count < 2 ? 0 : Math.sqrt(sum / (count - 1));
    }

    public static List<List<Double>> normalizeRatings(List<List<Integer>> ratings, List<Double> userMeans, List<Double> userDeviations) {
        List<List<Double>> normalizedRatings = new ArrayList<>();
        for (int i = 0; i < ratings.size(); i++) {
            List<Integer> userRatings = ratings.get(i);
            double mean = userMeans.get(i);
            double deviation = userDeviations.get(i);
            List<Double> normalizedUserRatings = new ArrayList<>();
            for (int rating : userRatings) {
                if (rating != 0) {
                    double normalizedRating = (rating - mean) / deviation;
                    normalizedUserRatings.add(normalizedRating);
                } else {
                    normalizedUserRatings.add(Double.NaN);
                }
            }
            normalizedRatings.add(normalizedUserRatings);
        }
        return normalizedRatings;
    }

    public static List<Double> calculateSongMeans(List<List<Double>> normalizedRatings) {
        List<Double> songMeans = new ArrayList<>();
        for (int i = 0; i < normalizedRatings.get(0).size(); i++) {
            double sum = 0;
            int count = 0;
            for (List<Double> userRatings : normalizedRatings) {
                double rating = userRatings.get(i);
                if (!Double.isNaN(rating)) {
                    sum += rating;
                    count++;
                }
            }
            double mean = count > 0 ? sum / count : Double.NaN;
            songMeans.add(mean);
        }
        return songMeans;
    }

    public static List<Double> calculateSongDeviations(List<List<Double>> normalizedRatings, List<Double> songMeans) {
        List<Double> songDeviations = new ArrayList<>();
        for (int i = 0; i < normalizedRatings.get(0).size(); i++) {
            double sumOfSquares = 0;
            int count = 0;
            double mean = songMeans.get(i);
            for (List<Double> userRatings : normalizedRatings) {
                double rating = userRatings.get(i);
                if (!Double.isNaN(rating)) {
                    sumOfSquares += Math.pow(rating - mean, 2);
                    count++;
                }
            }
            double variance = count > 1 ? sumOfSquares / (count - 1) : Double.NaN;
            double deviation = !Double.isNaN(variance) ? Math.sqrt(variance) : Double.NaN;
            songDeviations.add(deviation);
        }
        return songDeviations;
    }

    public static void writeOutput(String filename, List<String> outputLines) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (String line : outputLines) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}