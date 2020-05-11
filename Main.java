package readability.git;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    static String[] ages = {"6", "7", "9", "10", "11", "12", "13",
            "14", "15", "16", "17", "18", "24", "24+"};

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);

        long[] totalData = {0, 0, 0, 0, 0}; //characters, words, sentences, syllables and polysyllables

        while (scanner.hasNextLine()) {
            long[] currentData = parseData(scanner.nextLine());
            for (int i = 0; i < totalData.length; i++) {
                totalData[i] += currentData[i];
            }
        }
        scanner.close();

        System.out.println("Words: " + totalData[1]);
        System.out.println("Sentences: " + totalData[2]);
        System.out.println("Characters: " + totalData[0]);
        System.out.println("Syllables: " + totalData[3]);
        System.out.println("Polysyllables: " + totalData[4]);

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
/*
        Scanner cin = new Scanner(System.in);
        String chooseScore = scanner.next();
        cin.close();
*/

        System.out.println("all");
        String chooseScore = "all";

        System.out.println();

        switch (chooseScore) {
            case "ARI":
                double ari = getAutomatedReadabilityIndex(totalData);
                averageAge(new double[]{ari});
                break;
            case "FK":
                double fk = getFleschKincaidReadabilityIndex(totalData);
                averageAge(new double[]{fk});
                break;
            case "SMOG":
                double smog = getSMOGIndex(totalData);
                averageAge(new double[]{smog});
                break;
            case "CL":
                double cl = getColemanLiauIndex(totalData);
                averageAge(new double[]{cl});
                break;
            case "all":
                double ariAll = getAutomatedReadabilityIndex(totalData);
                double fkAll = getFleschKincaidReadabilityIndex(totalData);
                double smogAll = getSMOGIndex(totalData);
                double clAll = getColemanLiauIndex(totalData);
                averageAge(new double[]{ariAll, fkAll, smogAll, clAll});
                break;
        }
    }

    public static long[] parseData(String str) {
        long[] data = {0, 0, 0, 0, 0}; //characters, words, sentences, syllables and polysyllables
        data[0] = str.replaceAll("\\s+", "").length(); //characters
        String[] sentences = str.trim().split("[.!?]");
        data[2] = sentences.length; //sentences
        for (String sen :
                sentences) {
            String[] wordsInOneSen = sen.trim().split("\\s+");
            data[1] += wordsInOneSen.length; //words
            for (String word :
                    wordsInOneSen) {
                word = word.toLowerCase();
                word = word.replaceAll(",", "");
                if (word.charAt(word.length() - 1) == 'e') {
                    word = word.substring(0, word.length() - 1);
                }
                word = word.replaceAll("[aeiouy][aeiouy]+", "a");
                word = word.replaceAll("[^aeiouy]+", "");
                if (word.length() == 0) {
                    data[3] += 1; //syllables
                } else {
                    data[3] += word.length(); //syllables
                }
                if (word.length() > 2) {
                    data[4]++; //polysyllables
                }
            }
        }
        return data;
    }

    public static double getAutomatedReadabilityIndex(long[] data) {
        double score =  4.71 * (data[0] / (double) data[1]) +
                0.5 * (data[1] / (double) data[2]) - 21.43;
        System.out.print("Automated Readability Index: ");
        System.out.printf("%.2f", score);
        System.out.println(" (about " + ages[(int) Math.ceil(score) - 2] + " year olds).");
        return score;
    }

    public static double getFleschKincaidReadabilityIndex(long[] data) {
        double score = 0.39 * (data[1] / (double) data[2]) +
                11.8 * (data[3] / (double) data[1]) - 15.59;
        System.out.print("Flesch–Kincaid readability tests: ");
        System.out.printf("%.2f", score);
        System.out.println(" (about " + ages[(int) Math.ceil(score) - 2] + " year olds).");
        return score;
    }

    public static double getSMOGIndex(long[] data) {
        double score = 1.043 * Math.sqrt(data[4] * (30 / (double) data[2])) + 3.1291;
        System.out.print("Simple Measure of Gobbledygook: ");
        System.out.printf("%.2f", score);
        System.out.println(" (about " + ages[(int) Math.ceil(score) - 2] + " year olds).");
        return score;
    }

    public static double getColemanLiauIndex(long[] data) {
        double L = data[0] / (double) data[1] * 100;
        double S = data[2] / (double) data[1] * 100;
        double score = 0.0588 * L - 0.296 * S - 15.8;
        System.out.print("Coleman–Liau index: ");
        System.out.printf("%.2f", score);
        System.out.println(" (about " + ages[(int) Math.ceil(score) - 2] + " year olds).");
        return score;
    }

    public static void averageAge(double[] scores) {
        System.out.println();
        double sumAges = 0;
        for (double score:
                scores) {
            sumAges += Integer.parseInt(ages[(int) Math.ceil(score) - 3]);
        }
        System.out.println("This text should be understood in average by " + sumAges / scores.length + " year olds.");
    }
}
