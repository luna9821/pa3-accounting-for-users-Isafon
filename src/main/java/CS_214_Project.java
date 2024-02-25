

import java.io.*;
import java.util.*;

public class CS_214_Project {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java CS_214_Project <songs_file> <ratings_file> <output_file>");
            return;
        }

        String songsFile = args[0];
        String ratingsFile = args[1];
        String outputFile = args[2];

        List<String> songs = readSongs(songsFile);
        List<List<Integer>> ratings = readRatings(ratingsFile);

        // Identify and remove uncooperative users
        removeUncooperativeUsers(ratings);

        // Normalize ratings
        List<List<Double>> normalizedRatings = normalizeRatings(ratings);

        // Calculate song statistics
        List<SongStatistics> songStats = calculateSongStatistics(songs, normalizedRatings);

        // Write output
        writeOutput(outputFile, songStats);
    }

    static List<String> readSongs(String filename) throws IOException {
        List<String> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                songs.add(line.trim());
            }
        }
        return songs;
    }

    static List<List<Integer>> readRatings(String filename) throws IOException {
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
    

    static void removeUncooperativeUsers(List<List<Integer>> ratings) {
        Iterator<List<Integer>> iterator = ratings.iterator();
        while (iterator.hasNext()) {
            List<Integer> userRatings = iterator.next();
            if (isUncooperative(userRatings)) {
                iterator.remove();
            }
        }
    }

    private static boolean isUncooperative(List<Integer> ratings) {
        int sum = 0;
        int count = 0;
        Integer firstRating = null;
        for (Integer rating : ratings) {
            if (rating != 0) {
                sum += rating;
                count++;
                if (firstRating == null) {
                    firstRating = rating;
                } else if (!firstRating.equals(rating)) {
                    return false;
                }
            }
        }
        return count == 0 || sum == firstRating * count;
    }

    static List<List<Double>> normalizeRatings(List<List<Integer>> ratings) {
        List<List<Double>> normalizedRatings = new ArrayList<>();
        for (List<Integer> userRatings : ratings) {
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

    private static List<SongStatistics> calculateSongStatistics(List<String> songs, List<List<Double>> normalizedRatings) {
        List<SongStatistics> songStats = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            String song = songs.get(i);
            double mean = calculateMeanForSong(normalizedRatings, i);
            double stdDev = calculateStandardDeviationForSong(normalizedRatings, mean, i);
            songStats.add(new SongStatistics(song, mean, stdDev));
        }
        return songStats;
    }

    private static double calculateMeanForSong(List<List<Double>> ratings, int songIndex) {
        double sum = 0;
        int count = 0;
        for (List<Double> userRatings : ratings) {
            Double rating = userRatings.get(songIndex);
            if (rating != 0) {
                sum += rating;
                count++;
            }
        }
        return count == 0 ? Double.NaN : sum / count;
    }

    private static double calculateStandardDeviationForSong(List<List<Double>> ratings, double mean, int songIndex) {
        double sum = 0;
        int count = 0;
        for (List<Double> userRatings : ratings) {
            Double rating = userRatings.get(songIndex);
            if (rating != 0) {
                sum += Math.pow(rating - mean, 2);
                count++;
            }
        }
        return count < 2 ? Double.NaN : Math.sqrt(sum / (count - 1));
    }

    private static void writeOutput(String filename, List<SongStatistics> songStats) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (SongStatistics stat : songStats) {
                writer.write(stat.toString());
                writer.newLine();
            }
        }
    }

    private static class SongStatistics {
        String song;
        double mean;
        double stdDev;

        SongStatistics(String song, double mean, double stdDev) {
            this.song = song;
            this.mean = mean;
            this.stdDev = stdDev;
        }

        @Override
        public String toString() {
            return song + " " + (Double.isNaN(mean) ? "UNDEFINED" : mean) + " " + (Double.isNaN(stdDev) ? "UNDEFINED" : stdDev);
        }
    }
}