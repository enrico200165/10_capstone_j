import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


class CharCP {
    public CharCP(char c,int cp) { this.c = c; this.cp = cp; this.offset = -1;}
    public CharCP(char c,int cp, int offset) { this.c = c; this.cp = cp; this.offset = offset;}
    public char c;
    public int cp;
    public boolean cp_patched = false;
    public boolean char_patched = false;
    int offset;
}


public class readTextFile {

    static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus";


    // --------------------------------------------------------------
    private static String findDir(String origDir) {
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
    static public boolean isCodePointUndesirable(int cp ) {
    // --------------------------------------------------------------
    // https://stackoverflow.com/questions/220547/printable-char-in-java
        boolean ret = false;

        switch (cp) {
            case 65533: ret = true; break;
            default:
                break;
        }

        switch (Character.getType(cp)) {
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

        return ret;
    }


    // --------------------------------------------------------------
    static public CharCP replaceCharCP(char c, int cp) {
        // ----------------------------------------------------------

        CharCP ccp = new CharCP(c,cp);
        ccp.char_patched = true;
        ccp.cp_patched = true;

        // java.lang.Character.toChars(int codePoint)

        switch (cp) {
            // case : {  ;ccp.patched = true; break;}
            case 8211:
            case 8212:
                {  ccp.cp = (int)'-';
                ccp.char_patched= true; break;}
            default:
                ccp.cp_patched = false;
                break;
        }
        if (ccp.cp_patched) {
            ccp.c = (char) ccp.cp;
            return ccp;
        }



        switch (c) {
            // case : { ccp.char = '' ;ccp.char_patched = true; break;}
            case 'ø': { ccp.c = '|' ; ccp.char_patched = true; break;}
            default: {
                ccp.char_patched = false;
                break;
            }
        }
        if (ccp.char_patched) {
            ccp.cp = (int) ccp.c;
            return ccp;
        }

        return ccp;
    }




    static public boolean isCharUndesirable(char c) {
        boolean ret = false;
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);

        ret = ret || (block == Character.UnicodeBlock.SPECIALS);

        return ret;
    }


    // --------------------------------------------------------------
    static String replaceNonPrintable(String myString) {
    // --------------------------------------------------------------
    // https://stackoverflow.com/questions/6198986/how-can-i-replace-non-printable-unicode-characters-in-java
    // replaceAll("\\p{C}", "?");
    // A non-regex approach is the only way I know to properly handle \p{C}:

        int len = myString.length();
        StringBuilder newString = new StringBuilder(len);
        for (int offset = 0; offset < myString.length(); ) {

            // don't change in a hurry 3 lines below
            int cp = myString.codePointAt(offset);
            offset += Character.charCount(cp);
            char c = (char)cp;
            char c2 = (Character.toChars(cp))[0];
            String charStr = "";
            if (c == c2) {
                charStr = ""+c;
            } else {
                log.error("errore interno");
                System.exit(99);
            }


            // is it surely OK?
            if (charStr.matches("[\\w]"))  {
                newString.append(Character.toChars(cp));
                continue;
            }
            if (charStr.matches("[ .,:;!?#(){}\\[\\]'`‘\"]"))  {
                newString.append(Character.toChars(cp));
                continue;
            }
            //operations and currency
            if (charStr.matches("[+=_\\^$|£$%&/§@°]")
                    || c == '-' || c == '*')  {
                newString.append(Character.toChars(cp));
                continue;
            }
            // quotations
            if (charStr.matches("[’”“]"))  {
                newString.append(Character.toChars(cp));
                continue;
            }


            // --- replace strange with similar normal
            CharCP ccp = replaceCharCP(c,cp);
            if (ccp.char_patched || ccp.cp_patched) {
                c = ccp.c;
                cp = ccp.cp;
                newString.append(Character.toChars(cp));
                continue;
            }

            log.info("non trivial char: '"+c+"' "+" codepoint: "+cp);


            if (isCodePointUndesirable(cp) )  {
                log.debug("");
                continue;
            }
            if (isCharUndesirable(c) )  {
                log.debug("");
                continue;
            }


            newString.append(Character.toChars(cp));
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
                // log.info("line: "+lineNum+ " col: "+i +" non printable char, code: "+charCode);
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
            log.debug("breakpoint debug");
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
            log.error("directory not found");
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
            log.info("read nr lines: " + i);

            Iterator it = charsFound.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                log.info("char code: " + pair.getKey()+ " " + String.format("0x%04X", pair.getKey())
                        +  "'"+pair.getValue() +  "'" + " occurrences " + pair.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e.getMessage());
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
            log.error("directory not found");
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
                        log.error("line " + i + " len " + str.length() + " " + str.substring(0, 32) + "...");
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
            log.error("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
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
            log.error("directory not found");
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
                        log.info("new max length: line " + i + " len " + str.length() + " " + str.substring(0, 32) + "...");
                    }
                }

                String line = replaceNonPrintable(str);
                writer.write(line+ System.lineSeparator());
            }
            log.error("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
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
            log.error("directory not found");
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
                        log.error("line " + i + " len " + str.length() + " " + str.substring(0, 32) + "...");
                    }
                }
                if (str.length() > 1022) {
                    if (!str.endsWith(System.lineSeparator()))
                        str += System.lineSeparator();
                    writer.write(str);
                }
            }
            log.error("read nr lines: " + i + " max len " + maxlung);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
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

    final static Logger log = Logger.getLogger(readTextFile.class);

}
