import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OutputGeneration {
    public static void generateOutput(List<String> songTitles, List<Double> normalizedMeans, List<Double> normalizedStdDevs, String outputFile) throws IOException {
        List<String> outputLines = IntStream.range(0, songTitles.size())
            .mapToObj(i -> formatSongOutput(songTitles.get(i), normalizedMeans.get(i), normalizedStdDevs.get(i)))
            .collect(Collectors.toList());
    
        Files.write(Paths.get(outputFile), outputLines, StandardOpenOption.CREATE);
    }

    private static String formatSongOutput(String songTitle, Double normalizedMean, Double normalizedStdDev) {
        String meanStr = normalizedMean.isNaN() ? "UNDEFINED" : String.format("%.2f", normalizedMean);
        String stdDevStr = normalizedStdDev.isNaN() ? "UNDEFINED" : String.format("%.2f", normalizedStdDev);
        return songTitle + ", " + meanStr + ", " + stdDevStr;
    }
}
