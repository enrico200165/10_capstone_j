import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class readTextFile {
    public static void main(String[] args){

        try {
            File fileDir = new File("V:\\data\\pers_dev\\data_dev\\capstone_data\\data_in\\corpus\\en_US.news_full_1_1.txt");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            String str;
            int maxlung = 0;
            long i = 1;
            while ((str = in.readLine()) != null) {
                i++;
                if (str.length() > maxlung ) {
                    maxlung = str.length();
                    System.out.println("nex max length: "+ maxlung);
                    if (str.length() > 8)
                        System.out.println(str.substring(0,7));
                    System.out.println("line "+i+" len "+str.length());
                }
            }
            in.close();
            System.out.println("read nr lines: "+i+ " max len "+ maxlung);
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}