import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * WHole approach quickly copied and adjusted from internet, this class is a service
 * class, not examined yet logic behind it IT SEEMS useful to manage the fact that
 * characters have different length in bytes
 */
class CharCP {

    public CharCP(char c,int cp) { this.c = c; this.cp = cp; this.offset = -1;}
    public CharCP(char c,int cp, int offset) { this.c = c; this.cp = cp; this.offset = offset;}

    public char c;
    public int cp;
    public boolean patched = false;

    int offset;
}



/* ----------------------------------------------------------------------------
*
*
---------------------------------------------------------------------------* */
public class readTextFile {

    // static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus_subset";
    //
    static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus_full";


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


    // ##############################################################
    //                 LINES EXCESSIVE LENGTH
    // ##############################################################

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
    public static void breakLines(String fname )
    // --------------------------------------------------------------
    {
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



    // ##############################################################
    //                 UNUSUAL CHARACTERS
    // ##############################################################

    // --------------------------------------------------------------
        static public CharCP replaceCharCP(char c, int cp)
    // --------------------------------------------------------------
    {

        CharCP ccp = new CharCP(c,cp);


        ccp.patched = true;

        // --- by codepoint ---
        switch (cp) {

            // map to Blank
            case 174:
            case 183:
            case 9632:
            case 9670:
            case 0x1F354: // char: 
            case 0x1F35F: // char: 
            case 0x1F378: // char: 
            case 0x1F37B: // char: 
            case 0x1F380: // char: 
            case 0x1F3B6: // char: 
            case 0x1F3C0: // char: 
            case 0x1F440: // char: 
            case 0x1F466: // char: 
            case 0x1F48B: // char: 
            case 0x1F491: // char: 
            case 0x1F494: // char: 
            case 0x1F499: // char: 
            case 0x1F49F: // char: 
            case 0x1F4A4: // char: 
            case 0x1F525: // char: 
            case 0x1F601: // char: 
            case 0x1F609: // char: 
            case 0x1F60A: // char: 
            case 0x1F612: // char: 
            case 0x1F616: // char: 
            case 0x1F618: // char: 
            case 0x1F61C: // char: 
            case 0x1F621: // char: 
            case 0x1F622: // char: 
            case 0x1F625: // char: 
            case 0x1F62D: // char: 
            case 0x1F630: // char: 
            case 0x1F633: // char: 
            case 0x2027: // char: ‧
            case 0x221A: // char: √
            case 0x2607: // char: ☇
            case 0x263A: // char: ☺
            case 0x263C: // char: ☼
            case 0x2661: // char: ♡
            case 0x2665: // char: ♥
            case 0x266C: // char: ♬
            case 0x2764: // char: ❤
            {
                ccp.cp = (int)' ';
                ccp.c = (char)ccp.cp;
                return ccp;
            }


            case 0x2013: // char: –
            case 0x2014: // char: —
            case 0x2022: // char: •
            case 0x2026: // char: …
            {
                ccp.cp = (int)'-';
                ccp.c = (char)ccp.cp;
                return ccp;
            }

            case 6529: { ccp.cp = (int)','; ccp.c = (char)ccp.cp; return ccp; }
            case 8260: { ccp.cp = (int)'/'; ccp.c = (char)ccp.cp; return ccp; }

            default:   { break;  }
        }


        // --- by character ----
        switch (c) {
            case 'ø': { ccp.c = '|' ; ccp.cp = (int) ccp.c; return ccp;}
            default: { break; }
        }


        ccp.patched = false;
        return ccp;
    }


    // --------------------------------------------------------------
    static public boolean isCharToRemove(char c, int cp )
    // --------------------------------------------------------------
    // does it overlap with the replacement? probablu should preeced it
    {
        // https://stackoverflow.com/questions/220547/printable-char-in-java

        switch (cp) {
            case 65533:
                return true;
            default:  break;
        }


        // --- by block ---
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if ( !( block == Character.UnicodeBlock.BASIC_LATIN
        )
        ) {
            return true;
        }

        // --- by type ---
        switch (Character.getType(cp)) {
            case Character.CONTROL:     // \p{Cc}
            case Character.FORMAT:      // \p{Cf}
            case Character.PRIVATE_USE: // \p{Co}
            case Character.SURROGATE:   // \p{Cs}
            case Character.UNASSIGNED:  { // \p{Cn}
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }


    // --------------------------------------------------------------
    static String replaceProblematicCharsInString(String myString
            , Map<Integer ,Integer> unsualCharsFound)
    // --------------------------------------------------------------
    // https://stackoverflow.com/questions/6198986/how-can-i-replace-non-printable-unicode-characters-in-java
    // replaceAll("\\p{C}", "?");
    // A non-regex approach is the only way I know to properly handle \p{C}:
    {

        int len = myString.length();
        StringBuilder newString = new StringBuilder(len);

        for (int offset = 0; offset < myString.length(); ) {

            // don't change in a hurry below
            int cp = myString.codePointAt(offset);
            offset += Character.charCount(cp);
            char c = (char)cp;

            //char c2 = (Character.toChars(cp))[0];
            //String charStr = "";
            //if (c == c2) { charStr = ""+c; }
            // else { log.error("errore interno"); System.exit(99); }

            assert(Character.toChars(cp).equals(c));
            String charStr = ""+c;


            // --- is it surely OK? ---
            if ( cp <= 255 ) {
                newString.append(Character.toChars(cp));
                continue;
            }
            // --- is it OK even thoug > 255 ---
            if ( false
                    || charStr.matches("[\\w]")
                    || charStr.matches("[ .,:;!?#(){}\\[\\]'`‘\"]")
                    || charStr.matches("[+=¼_\\^$|£€$%&/§@°~»]") //math and currency
                    || charStr.matches("[’”“]") // quotations
                    )  {
                newString.append(Character.toChars(cp));
                nrCharsOkAnyway++;
                continue;
            }


            // --- can we replace it with similar more common one ---
            CharCP ccp = replaceCharCP(c,cp);
            if (ccp.patched) {
                char oldC = c; int oldCp = cp;
                c = ccp.c;
                cp = ccp.cp;
                log.info("replace: '"+oldC+"' "+oldCp+" with '"+c+"' "+cp);
                newString.append(Character.toChars(cp));
                nrCharReplaced++;
                continue;
            }

            // --- ccan we ignore it ? ---
            if (isCharToRemove(c,cp) )  {
                log.debug("ignoring: '"+c+"' "+cp);
                nrCharsIgnored++;
                continue;
            }

            // unusual and unmanaged, report it
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
    public static void replaceUnusualChars(String fname, String fnameOut
            , Map<Integer,Integer> unsualCharsFound) {
    // --------------------------------------------------------------
        String fpathIn = null;
        String fpathOut = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            log.error("directory not found");
            return;
        } else {
            fpathIn = corpusDir + "\\" + fname;
            fpathOut  = corpusDir + "\\" + fnameOut;
        }

        OutputStreamWriter writer = null;
        File fileDir = null;
        BufferedReader in = null;
        unsualCharsFound.clear();
        log.info("input file:"+fpathIn);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpathOut), StandardCharsets.UTF_8);
            fileDir = new File(fpathIn);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String str;
            int maxlung = 0;
            long i = 0;
            while ((str = in.readLine()) != null) {
                i++;
                if ((i % 1000) == 0) log.info("process line: "+i+" \""+str.substring(0,Math.min(0,23))+"...\"");
                String line = replaceProblematicCharsInString(str,unsualCharsFound);
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
                log.info("\noutput file: "+fpathIn
                      +"\ninput was:   "+fpathIn);
            } catch (Exception e) {
            }
        }
    }


    // --------------------------------------------------------------
    public static void dumpUnusual(Map<Integer,Integer> unsualCharsFound)
    // --------------------------------------------------------------
    {

        Iterator it = unsualCharsFound.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            log.info("char code: " + pair.getKey()+ " " + String.format("0x%04X", pair.getKey())
                    +  " '"+(char)(int)pair.getKey() +  "' " + " occurrences " + pair.getValue());
        }
        log.info("unual char types: "+ unmanagedChars.size());
    }

    // --------------------------------------------------------------
    public static void dumpUnusualCharsJavaCode(Map<Integer,Integer> unsualCharsFound)
    // --------------------------------------------------------------
    {

        Iterator it = unsualCharsFound.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("case " + String.format("0x%04X", pair.getKey())
                    + ": { break; }  // char: "+(char)(int)pair.getKey()
                    + String.format("0x%04X", pair.getKey())+" dec "+ pair.getKey());
        }
        log.info("unual char types: "+ unmanagedChars.size());
    }




    // ##############################################################
    //                   SENTENCE MARKERS
    // ##############################################################

    // --------------------------------------------------------------
        static String addBeginEndMarkers(String line)
    // --------------------------------------------------------------
    {
        String startm = "sss";
        String endm   = "eee";
        String endStartM = " "+endm+ " "+ startm+" ";

        Set<String> dotNotEndOFSentences = new HashSet<String>();
        dotNotEndOFSentences.add("mr");
        dotNotEndOFSentences.add("sr");
        dotNotEndOFSentences.add("jr");
        dotNotEndOFSentences.add("jan");
        dotNotEndOFSentences.add("feb");
        dotNotEndOFSentences.add("mar");
        dotNotEndOFSentences.add("apr");
        dotNotEndOFSentences.add("may");
        dotNotEndOFSentences.add("jun");
        dotNotEndOFSentences.add("jul");
        dotNotEndOFSentences.add("aug");
        dotNotEndOFSentences.add("sep"); dotNotEndOFSentences.add("sept");
        dotNotEndOFSentences.add("oct");
        dotNotEndOFSentences.add("nov");
        dotNotEndOFSentences.add("dec");

        dotNotEndOFSentences.add("p.m");





        // ?! seem quick and easy, dealt with here
        // not . that can signal just an abbreviation
        String workStr = line.replaceAll("[!?] +",endStartM);


        // let's try to manage the dot
        ArrayList<String> chuncksTmpStore = new ArrayList<String>();
        String[] endWithDot = workStr.split("\\.");
        int i = -1;
        for (String s : endWithDot) {
            i++;
            String[] words = s.split("\\s");
            // if (words.length-1 < 0) log.info("");
            String lastWord = (words.length-1 >= 0) ? words[words.length-1] : s;
            // log.info("last word of \""+s +"\" is '"+lastWord+"'");
            if (dotNotEndOFSentences.contains(lastWord.toLowerCase())
                //    || lastWord.equals(lastWord.toUpperCase()) // probably acronym
            ) {
                chuncksTmpStore.add(s+".");
                continue;
            }


            boolean added = false;
            for (String x : dotNotEndOFSentences) {
                if (s.toLowerCase().matches("[^a-z]*"+x)) {
                    chuncksTmpStore.add(s+".");
                    added = true;
                    break;
                }
            }
            if (added) continue;

            if (i < (endWithDot.length-1))
                chuncksTmpStore.add(s+endStartM);
            else  // last subphrase
                chuncksTmpStore.add(s+" "+endm); // last chunck only end marker
        }

        String  replaced = startm + " ";
        for(String s : chuncksTmpStore) replaced += s;

        return replaced;
    }

    // --------------------------------------------------------------
    public static void addBeginEndSentenceMarkers(String fname, String fnameOut) {
    // --------------------------------------------------------------

        String fpathIn = null;  String fpathOut = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            log.error("directory not found");
            return;
        } else {
            fpathIn = corpusDir + "\\" + fname;
            fpathOut  = corpusDir + "\\" + fnameOut;
        }

        OutputStreamWriter writer = null;
        File fileDir = null;
        BufferedReader in = null;

        log.debug("input file:"+fpathIn);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpathOut), StandardCharsets.UTF_8);
            fileDir = new File(fpathIn);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                String line = addBeginEndMarkers(str);
                // log.info("original: "+str); log.info("modified: "+line);
                writer.write(line+ System.lineSeparator());
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                in.close();
                writer.close();
                log.info("\noutput file: "+fpathIn
                        +"\ninput was:   "+fpathIn);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }



    // --------------------------------------------------------------
    public static void sentenceMarkersAllFiles() {
    // --------------------------------------------------------------

        String markedTag = "MARKED";


        log.info("working dir: "+System.getProperty("user.dir"));

        String foundDir = findDir(corpusDir);
        File folder = new File(foundDir);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                // listFilesForFolder(fileEntry);
            } else {
                String fname = fileEntry.getName();
                if (!fname.matches(".*"+markedTag+".*")) {
                    String fnameClean = fname + markedTag + ".txt";
                    log.info("processing file: "+fname);
                    addBeginEndSentenceMarkers(fname , fnameClean);
                } else {
                    log.debug("ignored file: "+fname);
                }
            }
        }

    }


    // --------------------------------------------------------------
    public static void replaceUnusualCharsAllFiles() {
        // --------------------------------------------------------------

        String markedTag = "CLEANCHARS";


        log.info("working dir: "+System.getProperty("user.dir"));

        String foundDir = findDir(corpusDir);
        File folder = new File(foundDir);


        nrCharsOkAnyway = 0; nrCharReplaced = 0; nrCharsIgnored = 0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                // listFilesForFolder(fileEntry);
            } else {
                String fname = fileEntry.getName();
                if (!fname.matches(".*"+markedTag+".*")) {
                    String fnameClean = fname + markedTag + ".txt";
                    //unmanagedChars.clear();
                    replaceUnusualChars(fname ,fnameClean , unmanagedChars);
                } else {
                    log.debug("ignored file: "+fname);
                }
            }
        }
        dumpUnusual(unmanagedChars);
        log.info("========= Java code ===========");
        dumpUnusualCharsJavaCode(unmanagedChars);
    }

    public static void main(String[] args) {
        replaceUnusualCharsAllFiles();
    }

    static TreeMap<Integer,Integer> unmanagedChars = new TreeMap<Integer,Integer>();

    static int nrCharsOkAnyway;
    static int nrCharsIgnored;  // not replaced
    static int nrCharReplaced;


    final static Logger log = Logger.getLogger(readTextFile.class);

}
