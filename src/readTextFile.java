import java.io.*;
import java.nio.file.Files;

public class readTextFile {

    static String findDir(String origDir) {
        while ( !(new File((origDir)).exists() && (new File((origDir)).isDirectory()) )
                && origDir.length() >= 4) {
            if (origDir.startsWith("..\\")) {
                origDir = origDir.substring(3,origDir.length());
            } else {
                return null;
            }
        }
        return origDir;
    }


    public static void main(String[] args){

        String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus";
        String fname = "en_US.news_full_1_1.txt";
        String fpath = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            System.out.println("directory not found");
            return;
        } else {
            fpath = corpusDir+"\\"+fname;
        }


        File fileDir = null;
        BufferedReader in = null;
        try {
            fileDir = new File(fpath);
           in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            String str;
            int maxlung = 0;
            long i = 1;
            while ((str = in.readLine()) != null) {
                i++;
                if (str.length() > maxlung ) {
                    maxlung = str.length();
                    if (str.length() > 1024) {
                        System.out.println("line " + i + " len " + str.length()+" "+str.substring(0, 32)+"...");
                    }
                }
            }
            in.close();
            System.out.println("read nr lines: "+i+ " max len "+ maxlung);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }  catch (Exception e)  {
            System.out.println(e.getMessage());
        } finally {
            try {  in.close(); } catch(Exception e) {}
        }
    }
}