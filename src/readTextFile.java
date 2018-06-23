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
    static public CharCP replaceCharCP(char c, int cp) {
        // ----------------------------------------------------------

        CharCP ccp = new CharCP(c,cp);
        ccp.char_patched = true;
        ccp.cp_patched = true;

        // java.lang.Character.toChars(int codePoint)

        switch (cp) {
            // map to Blank
            case 174:
            case 183:
            case 8226:
            case 8230:
            case 9632:
            case 9670:
            case 9679:
                { ccp.cp = (int)' '; ccp.char_patched = true; break;}
            // case : {  ;ccp.patched = true; break;}
            // case : {  ;ccp.patched = true; break;}


            case 8211:
            case 8212: { ccp.cp = (int)'-'; ccp.char_patched= true; break;}
            case 6529: { ccp.cp = (int)','; ccp.char_patched = true; break;}
            case 8260: { ccp.cp = (int)'/'; ccp.char_patched = true; break;} // ⁄
            case 8730: { ccp.cp = (int)'√'; ccp.char_patched = true; break;}
            //case : { ccp.cp = (int)''; ccp.char_patched = true; break;}
            //case : { ccp.cp = (int)''; ccp.char_patched = true; break;}
            //case : { ccp.cp = (int)''; ccp.char_patched = true; break;}
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


    // --------------------------------------------------------------
    static public boolean ignoreCharCP(char c, int cp) {

        boolean ret = false;

        switch (cp) {
            // case : {  ;ccp.patched = true; break;}
            default:
                break;
        }



        switch (c) {
            // case 'ø': { ccp.c = '|' ; ccp.char_patched = true; break;}
            default: {
                break;
            }
        }
        return ret;
    }



    // --------------------------------------------------------------
    static public boolean isCharToRemove(char c, int cp ) {
        // https://stackoverflow.com/questions/220547/printable-char-in-java

        switch (cp) {
            case 65533: return true;
            default:  break;
        }


        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if ( false
                || block == Character.UnicodeBlock.SPECIALS) {
            return true;
        }


        switch (Character.getType(cp)) {
            case Character.CONTROL:     // \p{Cc}
            case Character.FORMAT:      // \p{Cf}
            case Character.PRIVATE_USE: // \p{Co}
            case Character.SURROGATE:   // \p{Cs}
            case Character.UNASSIGNED:  // \p{Cn}
                return true;
            default:
                break;
        }

        return false;
    }



    // --------------------------------------------------------------
    static String replaceProblematicChars(String myString, Map<Integer,Integer> unsualCharsFound) {
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
            if ( cp <= 255 ) {
                newString.append(Character.toChars(cp));
                continue;
            }
            if ( false
                    || charStr.matches("[\\w]")
                    || charStr.matches("[ .,:;!?#(){}\\[\\]'`‘\"]")
                    || charStr.matches("[+=¼_\\^$|£€$%&/§@°~»]") //math and currency
                    || charStr.matches("[’”“]") // quotations
                    )  {
                newString.append(Character.toChars(cp));
                continue;
            }


            // --- replace strange chars with similar OK ones
            CharCP ccp = replaceCharCP(c,cp);
            if (ccp.char_patched || ccp.cp_patched) {
                c = ccp.c;
                cp = ccp.cp;
                newString.append(Character.toChars(cp));
                continue;
            }


            if (isCharToRemove(c,cp) )  {
                log.debug("");
                continue;
            }


            java.lang.Object val;
            if ( ( val = unsualCharsFound.get(cp)) != null) {
                unsualCharsFound.put(cp, (int)val+1);
            } else {
                unsualCharsFound.put(cp,1);
            }
            // log.debug("non trivial char: '"+c+"' "+" codepoint: "+cp);
            newString.append(Character.toChars(cp));
        }
        return newString.toString();
    }


    // --------------------------------------------------------------
    static boolean hasNonPrintableChar(String s, long lineNum, Map<Integer,Integer> charsFound) {
        // --------------------------------------------------------------
        boolean found = false;
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!isCharToRemove(c,(int)c)) {
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
    public static void findNonPrintables(String fname,Map<Integer,Integer> unsualCharsFound) {
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
            unsualCharsFound.clear();
            while ((str = in.readLine()) != null) {
                i++;
                if (hasNonPrintableChar(str,i,unsualCharsFound))
                    linesWithNonPrint++;
            }
            log.info("read nr lines: " + i);

            Iterator it = unsualCharsFound.entrySet().iterator();
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
    public static void writePrintables(String fname, String fnameOut, Map<Integer,Integer> unsualCharsFound) {
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
        unsualCharsFound.clear();
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

                String line = replaceProblematicChars(str,unsualCharsFound);
                writer.write(line+ System.lineSeparator());
            }
            log.info("read nr lines: " + i + " max len " + maxlung);
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
        writePrintables(fname, fnameClean,unsualCharsFound);
        dumpUnusual(unsualCharsFound);
        //findNonPrintables(fnameClean);
        //breakLines();
        //longLines();
    }


    public static void dumpUnusual(Map<Integer,Integer> unsualCharsFound) {

        Iterator it = unsualCharsFound.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            log.info("char code: " + pair.getKey()+ " " + String.format("0x%04X", pair.getKey())
                    +  " '"+(char)(int)pair.getKey() +  "' " + " occurrences " + pair.getValue());
        }

    }

    static TreeMap<Integer,Integer> unsualCharsFound = new TreeMap<Integer,Integer>();

    final static Logger log = Logger.getLogger(readTextFile.class);

}
