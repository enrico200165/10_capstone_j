import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class readTextFile {

    static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus";


    // --------------------------------------------------------------
    static String findDir(String origDir) {
    // --------------------------------------------------------------
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


    // --------------------------------------------------------------
    static public boolean isCharUndesirable(char c ) {
    // --------------------------------------------------------------
    // https://stackoverflow.com/questions/220547/printable-char-in-java
        boolean ret = false;

        if ((int)c == 65533)
            return true;

        int codePoint =  (""+c).codePointAt(0);  // c.codePointAt(""+c);
        switch (Character.getType(codePoint)) {
            case Character.CONTROL:     // \p{Cc}
            case Character.FORMAT:      // \p{Cf}
            case Character.PRIVATE_USE: // \p{Co}
            case Character.SURROGATE:   // \p{Cs}
            case Character.UNASSIGNED:  // \p{Cn}
                ret = true;
                return true;
                //break;
            default:
                break;
        }

        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        ret = ret || Character.isISOControl(c) || c == KeyEvent.CHAR_UNDEFINED ||
                (block != null && block == Character.UnicodeBlock.SPECIALS);
        return ret;
    }


    // --------------------------------------------------------------
    static String replaceNonPrintable(String myString) {
    // --------------------------------------------------------------
    // https://stackoverflow.com/questions/6198986/how-can-i-replace-non-printable-unicode-characters-in-java
    // replaceAll("\\p{C}", "?");
    // A non-regex approach is the only way I know to properly handle \p{C}:

        StringBuilder newString = new StringBuilder(myString.length());
        for (int offset = 0; offset < myString.length(); ) {
            int codePoint = myString.codePointAt(offset);
            offset += Character.charCount(codePoint);

            if (isCharUndesirable((char)codePoint))
                continue;
            else
                newString.append(Character.toChars(codePoint));
/*
            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.CONTROL:     // \p{Cc}
                case Character.FORMAT:      // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE:   // \p{Cs}
                case Character.UNASSIGNED:  // \p{Cn}
                    //newString.append('?');
                   //  newString.append(' ');
                    break;
                default:
                    newString.append(Character.toChars(codePoint));
                    break;
            }
*/
        }
        return newString.toString();
    }


    // --------------------------------------------------------------
    static boolean hasNonPrintableChar(String s, long lineNum, Map<Integer,Integer> charsFound) {
    // --------------------------------------------------------------
        boolean found = false;
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!isCharUndesirable(c)) {
                found = true;
                int charCode = (int)(c);
                // System.out.println("line: "+lineNum+ " col: "+i +" non printable char, code: "+charCode);
                java.lang.Object val;
                if ( ( val = charsFound.get(charCode)) != null) {
                    charsFound.put(charCode, (int)val+1);
                } else {
                    charsFound.put(charCode,1);
                }
            }
        }
        return found;
    }


    // --------------------------------------------------------------
    static List<String> splitLineIfTooLong(String line, int maxLength) {
    // --------------------------------------------------------------
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


    // --------------------------------------------------------------
    public static void findNonPrintables(String fname ) {
    // --------------------------------------------------------------
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
            int linesWithNonPrint = 0;
            TreeMap<Integer,Integer> charsFound = new TreeMap<>();
            while ((str = in.readLine()) != null) {
                i++;
                if (hasNonPrintableChar(str,i,charsFound))
                    linesWithNonPrint++;
            }
            System.out.println("read nr lines: " + i);

            Iterator it = charsFound.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println("char code: " + pair.getKey()+ " " + String.format("0x%04X", pair.getKey())
                        + " occurrences " + pair.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    // --------------------------------------------------------------
    public static void breakLines(String fname ) {
    // --------------------------------------------------------------
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


    // --------------------------------------------------------------
    public static void writePrintables(String fname, String fnameOut) {
    // --------------------------------------------------------------
        String fpath = null;
        String fpathOut = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            System.out.println("directory not found");
            return;
        } else {
            fpath = corpusDir + "\\" + fname;
            fpathOut  = corpusDir + "\\" + fnameOut;
        }

        OutputStreamWriter writer = null;
        File fileDir = null;
        BufferedReader in = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpathOut), StandardCharsets.UTF_8);
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

                String line = replaceNonPrintable(str);
                writer.write(line+ System.lineSeparator());
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


    // --------------------------------------------------------------
    public static void longLines(String fname) {
    // --------------------------------------------------------------
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

    // --------------------------------------------------------------
    public static void main(String[] args) {
    // --------------------------------------------------------------

        String fname = "en_US.news_full_1_1.txt";
        String fnameClean = fname+".clean.txt";
        writePrintables(fname, fnameClean);
        findNonPrintables(fnameClean);
        //breakLines();
        //longLines();
    }
}

