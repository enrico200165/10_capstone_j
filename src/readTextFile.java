import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class readTextFile {

    static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus";


    // https://stackoverflow.com/questions/220547/printable-char-in-java
    public boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    static boolean hasNonPrintableChar(String s, long lineNum) {
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!isPrintableChar(c)) {
                System.out.println("line: "+lineNum+ " col: "+i +" non printable char, code: "+(integer)(c) );
                return true;
            }
        }
        return false;
    }


    public static void findNonPrintables() {
        String fname = "en_US.news_full_1_1.txt";
        String fpath = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            System.out.println("directory not found");
            return;
        } else {
            fpath = corpusDir + "\\" + fname;
        }

        File fileDir = null;
        BufferedReader in = null;
        try {
            fileDir = new File(fpath);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String str;
            long i = 1;
            while ((str = in.readLine()) != null) {
                i++;
                hasNonPrintableChar(str,i);
            }
            System.out.println("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                writer.close();
            } catch (Exception e) {
            }
        }
    }



    static List<String> splitLineIfTooLong(String line, int maxLength) {
        List<String> lines = new ArrayList<String>();

        String[] fragments = line.split("\\.\\r*\\n");

        if (fragments.length > 1) {
            System.out.println("breakpoint debug");
        }

        for (String frag : fragments) {
            while (frag.length() > maxLength) {
                String detach = frag.substring(0, maxLength - 1);
                if (!detach.endsWith(System.lineSeparator()))
                    detach += System.lineSeparator();
                lines.add(detach);
                frag = frag.substring(maxLength, frag.length());
            }
            if (!frag.endsWith(System.lineSeparator()))
                frag += System.lineSeparator();
            lines.add(frag);
        }
        return lines;
    }



    static String findDir(String origDir) {
        while (!(new File((origDir)).exists() && (new File((origDir)).isDirectory()))
                && origDir.length() >= 4) {
            if (origDir.startsWith("..\\")) {
                origDir = origDir.substring(3, origDir.length());
            } else {
                return null;
            }
        }
        return origDir;
    }

    static List<String> splitLineIfTooLong(String line, int maxLength) {
        List<String> lines = new ArrayList<String>();

        String[] fragments = line.split("\\.\\r*\\n");

        if (fragments.length > 1) {
            System.out.println("breakpoint debug");
        }

        for (String frag : fragments) {
            while (frag.length() > maxLength) {
                String detach = frag.substring(0, maxLength - 1);
                if (!detach.endsWith(System.lineSeparator()))
                    detach += System.lineSeparator();
                lines.add(detach);
                frag = frag.substring(maxLength, frag.length());
            }
            if (!frag.endsWith(System.lineSeparator()))
                frag += System.lineSeparator();
            lines.add(frag);
        }
        return lines;
    }


    public static void breakLines() {
        String fname = "en_US.news_full_1_1.txt";
        String fpath = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            System.out.println("directory not found");
            return;
        } else {
            fpath = corpusDir + "\\" + fname;
        }

        OutputStreamWriter writer = null;
        File fileDir = null;
        BufferedReader in = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpath + ".shortened_lines.txt"), StandardCharsets.UTF_8);
            fileDir = new File(fpath);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String str;
            int maxlung = 0;
            long i = 1;
            while ((str = in.readLine()) != null) {
                i++;
                if (str.length() > maxlung) {
                    maxlung = str.length();
                    if (str.length() > 1024) {
                        System.out.println("line " + i + " len " + str.length() + " " + str.substring(0, 32) + "...");
                    }
                }
                List<String> lines = splitLineIfTooLong(str, 1022);
                for (String line : lines) {
                    if (!line.endsWith(System.lineSeparator())) {
                        line += System.lineSeparator();
                    }
                    writer.write(line);
                }
            }
            System.out.println("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                writer.close();
            } catch (Exception e) {
            }
        }
    }


    public static void longLines() {
        String fname = "en_US.news_full_1_1.txt";
        String fpath = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            System.out.println("directory not found");
            return;
        } else {
            fpath = corpusDir + "\\" + fname;
        }

        OutputStreamWriter writer = null;
        File fileDir = null;
        BufferedReader in = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpath + ".long_only.txt"), StandardCharsets.UTF_8);
            fileDir = new File(fpath);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String str;
            int maxlung = 0;
            long i = 1;
            while ((str = in.readLine()) != null) {
                i++;
                if (str.length() > maxlung) {
                    maxlung = str.length();
                    if (str.length() > 1024) {
                        System.out.println("line " + i + " len " + str.length() + " " + str.substring(0, 32) + "...");
                    }
                }
                if (str.length() > 1022) {
                    if (!str.endsWith(System.lineSeparator()))
                        str += System.lineSeparator();
                    writer.write(str);
                }
            }
            System.out.println("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        findNonPrintables();
        //breakLines();
        //longLines();
    }
}