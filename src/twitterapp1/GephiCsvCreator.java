package twitterapp1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by alexal.
 */
public class GephiCsvCreator {


    private static double[][] sample = { { 0.2,0.3,0.4} ,
                                  { 0.98, 0.99, 0.21} ,
                                  { 0.15, 0.135, 0.45 } };
    private static String[] sampleUsers = { "A", "B", "C"};


    /**
     * This method writes the given 2D array in a csv file.
     * @param values the array containing the data.
     * @param filename the name of the file. Should have extension .csv
     * @return true if write completed successfully.
     */
    public static boolean writeArrayToCsv(List<String> nodesNames, double[][] values, String filename){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            StringBuilder builder = new StringBuilder();
            builder.append("Source").append(",").append("Target").append(",").append("Weight");
            builder.append("\n");
            for ( int i = 0; i < values.length; i++ ){
                for ( int j = 0; j < values[i].length; j++){
                    builder.append(nodesNames.get(i)).append(",").append(nodesNames.get(j)).append(",").append(values[i][j]);
                    builder.append("\n");
                }
            }
            writer.write(builder.toString());
            writer.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }
/*
    public static void main(String[] args){
        System.out.println("Building with sample...");
        //writeArrayToCsv(Arrays.asList(sampleUsers),sample,"Sample.csv");

    }
*/


}
