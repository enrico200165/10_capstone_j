
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Whole approach quickly copied and adjusted from internet, this class is a service
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
public class AdjustTextFiles {

    // static  String corpusDir = "..\\..\\..\\..\\..\\data_dev\\capstone_data\\data_in\\corpus_subset";
    static  String corpusDir = "..\\..\\..\\..\\..\\dev_data\\capstone_data\\data_in\\corpus_full";


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

        // by codepoint
        switch (cp) {
            case 65533:
                return true;
            default:  break;
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

        for (int char_start_offset = 0; char_start_offset < myString.length(); ) {

            // read character correctly
            // don't change in a hurry below
            // EVGuess calculates each time the character size to move to the next
            int codePoint = myString.codePointAt(char_start_offset);
            char_start_offset += Character.charCount(codePoint);
            char c = (char)codePoint;
            assert(Character.toChars(codePoint).equals(c));

            // append to string
            String charStr = ""+c;

            // --- is it surely OK? ---
            if ( codePoint <= 255 ) {
                newString.append(Character.toChars(codePoint));
                continue;
            }
            // --- is it OK even thoug > 255 ---
            if ( false
                    || charStr.matches("[\\w]")
                    || charStr.matches("[ .,:;!?#(){}\\[\\]'`‘\"]")
                    || charStr.matches("[+=¼_\\^$|£€$%&/§@°~»]") //math and currency
                    || charStr.matches("[’”“]") // quotations
                    )  {
                newString.append(Character.toChars(codePoint));
                nrCharsOkAnyway++;
                continue;
            }


            // --- can we replace it with similar more common one ---
            CharCP ccp = replaceCharCP(c,codePoint);
            if (ccp.patched) {
                char oldC = c; int oldCp = codePoint;
                c = ccp.c;
                codePoint = ccp.cp;
                // log.info("replace: '"+oldC+"' "+oldCp+" with '"+c+"' "+codePoint);
                newString.append(Character.toChars(codePoint));
                nrCharReplaced++;
                continue;
            }

            // --- ccan we ignore it ? ---
            if (isCharToRemove(c,codePoint) )  {
                log.debug("ignoring: '"+c+"' "+codePoint);
                nrCharsIgnored++;
                continue;
            }

            // unusual and unmanaged, report it
            java.lang.Object val;
            if ( ( val = unsualCharsFound.get(codePoint)) != null) {
                unsualCharsFound.put(codePoint, (int)val+1);
            } else {
                unsualCharsFound.put(codePoint,1);
            }
            // log.debug("non trivial char: '"+c+"' "+" codepoint: "+cp);
            newString.append(Character.toChars(codePoint));
        }

        return newString.toString();
    }



    // --------------------------------------------------------------
    public static void replaceUnusualChars(String fname, String fnameOut
            , Map<Integer,Integer> unsualCharsFound) {
    // --------------------------------------------------------------
        String fpathIn = null; String fpathOut = null;

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
        log.info("processing files: \nin:  "+fpathIn+" \nout: "+fpathOut);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(fpathOut), StandardCharsets.UTF_8);
            fileDir = new File(fpathIn);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
            String lineOriginal;
            int maxlung = 0;
            long i = 0;
            while ((lineOriginal = in.readLine()) != null) {
                i++;
                if ((i % 5000) == 0)
                    log.debug("process line: "+i+" \""+lineOriginal.substring(
                            0,Math.min(23, lineOriginal.length()))+"...\"");
                String lineReplaced = replaceProblematicCharsInString(lineOriginal,unsualCharsFound);
                lineReplaced = addBeginEndMarkers(lineReplaced);
                writer.write(lineReplaced+ System.lineSeparator());
                log.info("\nO: "+ lineOriginal+"\nR: "+lineReplaced+"\n");
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


    static boolean isItMark(char chr) {
        return isItMark(Character.toString(chr));
    }
    static boolean isItMark(String chr) {
        return chr.matches("[\\.!\\?]");
    }

    static String lastChar(String s) {
        if (s == null || s.length() == 0)
            return "";
        else{
            int l = s.length();
            return(s.substring(l-1,l));
        }
    }

    // --------------------------------------------------------------
    static String addBeginEndMarkers(String line)
    // --------------------------------------------------------------
    {
        String startm = "sss";
        String endm   = "eee";
        String endStartM = " "+endm+ " "+ startm;

        // -- edge cases
        if (line.matches("[\\s]*"))
            return startm + " "+endm;

        if (line.matches("[\\s]*[\\w]+[\\s]*"))
            return startm + " "+line.trim()+ " "+endm;

        if (line.matches("[\\s]*[\\w]+\\.[\\s]*"))
            return startm + " "+line.trim()+ " "+endm;
        // --- End Edge Cases

        // ?! seem quick and easy, dealt with here
        // not . that can signal just an abbreviation
        String workStr = line.replaceAll("[!?] +",endStartM);

        // let's try to manage the dot inserting start end sentence markers

        ArrayList<String> chuncksTmpStore = new ArrayList<String>();
        int afterLastMatch = 0; int lastWhiteSpace = -1;

        for ( int i = 0; i < line.length(); ) {

            String c = line.substring(i, i+1);

            // track whitespace to look back
            if (c.matches("[\\s]")) {
                lastWhiteSpace = i;
            }

            // no dot nothing to do
            if (!isItMark(line.charAt(i))) {
                i++;
                continue;
            }

            // current char is a .

            // check character after
            if (i+1 < line.length())  {
                String restOfLine = line.substring(i+1,line.length());
                String c1 = line.substring(i+1, i+2);
                // gestione "sdsd xxx."
                if (c1.matches("[\"]")) {
                    // no whitespace after, \in the middle of a word,  do nothing
                    // TOTO MIGHT FAIL IF end line is considered whitespace
                    // and the  string is not trimmed
                    String chunkToAdd;
                    if (restOfLine.substring(1,restOfLine.length()).matches("[\\S]*")) {
                        chunkToAdd = line.substring(afterLastMatch, i) + "\"";
                    } else {
                        chunkToAdd = line.substring(afterLastMatch, i) + "\" "+endStartM;
                    }
                    chuncksTmpStore.add(chunkToAdd);
                    afterLastMatch = i+2;
                    i += 2;
                    continue;
                }
                if (c1.matches("[\\S]")) {
                    // no whitespace after, \in the middle of a word,  do nothing
                    // TOTO MIGHT FAIL IF end line is considered whitespace
                    // and the  string is not trimmed
                    i++;
                    continue;
                }

                // followed by  whitespace
                if  (i+2 < line.length()) { // check if  second  next is upper case
                    String c2 = line.substring(i+2, i+3);
                    if (c2.matches("[A-Z]"))  {
                        // look back that it is not an abbreviation
                        String thisWord = line.substring(lastWhiteSpace+1,i);
                        if (!acronymEtc.contains(thisWord)) {
                            String chunkToAdd = line.substring(afterLastMatch, i) + endStartM;
                            chuncksTmpStore.add(chunkToAdd);
                            i++;
                            afterLastMatch = i+1;
                            continue;
                        } else {
                            log.info("recognized as acronym or similar: "+thisWord);
                        }
                    }
                }
            }

            i++;
        }
        String chunk = line.substring(afterLastMatch,line.length());
        if (chunk != null && chunk.length() > 0)
            chuncksTmpStore.add(chunk);

        String  replaced = startm;
        int i = 0;
        while (i < chuncksTmpStore.size()-1) {
            replaced += " "+chuncksTmpStore.get(i);
            i++;
        }
        // manage the last chunk, that might end with . Sr..
        String lastWord = chuncksTmpStore.get(i);
        String lastWordTokens[] = chuncksTmpStore.get(i).split("[\\s]");
        String lastWordLastChunk = lastWordTokens[lastWordTokens.length-1];

        if (!isItMark(lastChar(lastWordLastChunk))
        && !lastWordLastChunk.matches(endm+"[\\s]*")) {
            String d = replaced+ " "+ lastWord+ " "+endm;
            return d;
        }
        if (lastWord.equals("."))  //edge case, just "."
            return replaced += " "+endm;

        lastWord = lastWord.substring(0,lastWord.length()-1);
        if (!acronymEtc.contains(lastWordLastChunk.substring(0,lastWordLastChunk.length()-1))) {
            replaced += " "+ lastWord + " "+endm;
        } else {
            replaced += " "+lastWord+". "+endm;
        }

        return replaced;
    }




    // --------------------------------------------------------------
    public static boolean replaceUnusualCharsAllFiles() {
        // --------------------------------------------------------------

        String markedTag = "CLEANCHARS";

        log.info("working dir: "+System.getProperty("user.dir"));

        String foundDir = findDir(corpusDir);
        if (foundDir == null) {
            log.error("failed to find corpus dir; searching from:\n "+corpusDir);
            return false;
        } else {
            log.info("working in dir:\n "+corpusDir);
            log.info("------------------------------------------");
        }
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

        return true;
    }


    public static void main(String[] args) {

        System.out.println("Working Directory = " +  System.getProperty("user.dir"));
        // acronymEtc = initNonEndTokens();
        replaceUnusualCharsAllFiles();
    }


    static TreeMap<Integer,Integer> unmanagedChars = new TreeMap<Integer,Integer>();

    static int nrCharsOkAnyway;
    static int nrCharsIgnored;  // not replaced
    static int nrCharReplaced;
    private static Set<String> acronymEtc = initNonEndTokens();;

    // ##############################################################
    //                   SENTENCE MARKERS
    // ##############################################################

    static Set<String> initNonEndTokens() {

        Set<String> dotNotEndOFSentencesSet = new HashSet<String>();

        dotNotEndOFSentencesSet.add("."); // per gestire ..
        dotNotEndOFSentencesSet.add(".."); // per gestire ...


        dotNotEndOFSentencesSet.add("mr");
        dotNotEndOFSentencesSet.add("sr");
        dotNotEndOFSentencesSet.add("jr");
        dotNotEndOFSentencesSet.add("st");

        dotNotEndOFSentencesSet.add("jan");
        dotNotEndOFSentencesSet.add("feb");
        dotNotEndOFSentencesSet.add("mar");
        dotNotEndOFSentencesSet.add("apr");
        dotNotEndOFSentencesSet.add("may");
        dotNotEndOFSentencesSet.add("jun");
        dotNotEndOFSentencesSet.add("jul");
        dotNotEndOFSentencesSet.add("aug");
        dotNotEndOFSentencesSet.add("sep"); dotNotEndOFSentencesSet.add("sept");
        dotNotEndOFSentencesSet.add("oct");
        dotNotEndOFSentencesSet.add("nov");
        dotNotEndOFSentencesSet.add("dec");

        dotNotEndOFSentencesSet.add("p.m");

        dotNotEndOFSentencesSet.add("a.s.a.p");    //	as	soon	as	possible
        dotNotEndOFSentencesSet.add("aaa");	    //	The	Agricultural	Adjustment	Act.	This	act	was
        dotNotEndOFSentencesSet.add("ACE");	    //	a	cool	experience
        dotNotEndOFSentencesSet.add("ad");	        //	awesome	dude
        dotNotEndOFSentencesSet.add("AFAIK");	    //	as	far	as	I	know
        dotNotEndOFSentencesSet.add("AFK");	    //	away	from	keyboard
        dotNotEndOFSentencesSet.add("am");	        //	am
        dotNotEndOFSentencesSet.add("AM");	        //	am
        dotNotEndOFSentencesSet.add("ANI");	    //	age	not	important
        dotNotEndOFSentencesSet.add("approx");	    //	approximately
        dotNotEndOFSentencesSet.add("appt");	    //	appointment
        dotNotEndOFSentencesSet.add("apt");	    //	apartment
        dotNotEndOFSentencesSet.add("Ave");	    //	Avenue
        dotNotEndOFSentencesSet.add("B.Y.O.B");	//	bring	your	own	bottle,	used	for	parties
        dotNotEndOFSentencesSet.add("BA");	        //	Bachelor	of	Arts
        dotNotEndOFSentencesSet.add("Blvd");	    //	Boulevard
        dotNotEndOFSentencesSet.add("BRB");	    //	be	right	back
        dotNotEndOFSentencesSet.add("BS");	        //	Bachelor	of	Science
        dotNotEndOFSentencesSet.add("c");	        //	cup/cups
        dotNotEndOFSentencesSet.add("c/o");	    //	care	of,	used	when	sending	mail	to
        dotNotEndOFSentencesSet.add("CCC");	    //	The	Civilian	Conservation	Corps.	Single	men	between
        dotNotEndOFSentencesSet.add("CEO");	    //	Chief	Executive	Officer
        dotNotEndOFSentencesSet.add("CFO");	    //	Chief	Financial	Officer
        dotNotEndOFSentencesSet.add("CMO");	    //	Chief	Marketing	Officer
        dotNotEndOFSentencesSet.add("CUL");	    //	see	you	later
        dotNotEndOFSentencesSet.add("CWA");	    //	The	Civil	Works	Administration.	Four	million	people
        dotNotEndOFSentencesSet.add("CWYL");	    //	chat	with	you	later
        dotNotEndOFSentencesSet.add("Cyn");	    //	Canyon
        dotNotEndOFSentencesSet.add("D.I.Y");	    //	Do	it	yourself
        dotNotEndOFSentencesSet.add("DC");	        //	Doctor	of	Chiropractic
        dotNotEndOFSentencesSet.add("D.C");	        //	Doctor	of	Chiropractic
        dotNotEndOFSentencesSet.add("dept");	    //	department
        dotNotEndOFSentencesSet.add("Dr");	        //	Drive
        dotNotEndOFSentencesSet.add("E");	        //	east
        dotNotEndOFSentencesSet.add("e.g");	    //	You	will	often	see	the	abbreviation	e.g.
        dotNotEndOFSentencesSet.add("E.T.A");	    //	estimated	time	of	arrival
        dotNotEndOFSentencesSet.add("est");	    //	established
        dotNotEndOFSentencesSet.add("etc");	    //	Etc,	often	seen	at	the	end	of
        dotNotEndOFSentencesSet.add("Etc");	    //	Etc,	often	seen	at	the	end	of
        dotNotEndOFSentencesSet.add("EVP");	    //	Executive	Vice	President
        dotNotEndOFSentencesSet.add("FDIC");	    //	The	Federal	Deposit	Insurance	Corp.	Since	banks
        dotNotEndOFSentencesSet.add("FHA");	    //	The	Federal	Housing	Administration.	This	organization	was
        dotNotEndOFSentencesSet.add("gal");	    //	gallon
        dotNotEndOFSentencesSet.add("i.e");	    //	Another	popular	abbreviation	we	use	in	daily
        dotNotEndOFSentencesSet.add("IIRC");	    // if	I	recall/remember	correctly
        dotNotEndOFSentencesSet.add("IQ");	        // ignorance	quotient
        dotNotEndOFSentencesSet.add("JD");	        //	Juris	Doctor
        dotNotEndOFSentencesSet.add("lb");	        //	pound/pounds
        dotNotEndOFSentencesSet.add("Ln");	        //	Lane
        dotNotEndOFSentencesSet.add("LOL");	    //	laugh	out	loud
        dotNotEndOFSentencesSet.add("M.PHIL");	    // or	MPHIL	-	Master	of	Philosophy
        dotNotEndOFSentencesSet.add("MA");	        // Master	of	Arts
        dotNotEndOFSentencesSet.add("MD");	        // Managing	Director
        dotNotEndOFSentencesSet.add("min");	    // minute	or	minimum
        dotNotEndOFSentencesSet.add("misc");	    // miscellaneous
        dotNotEndOFSentencesSet.add("Mr");	        // Mister
        dotNotEndOFSentencesSet.add("Mrs");	    // Mistress	(pronounced	Missus)
        dotNotEndOFSentencesSet.add("N");	        // north
        dotNotEndOFSentencesSet.add("n.b");	    // This	is	sometimes	written	at	the	end
        dotNotEndOFSentencesSet.add("NE");	        // northeast
        dotNotEndOFSentencesSet.add("no");	        // number
        dotNotEndOFSentencesSet.add("NP");	        // no	problem
        dotNotEndOFSentencesSet.add("NRA");	    // The	National	Recovery	Administration.	In	1933,	the
        dotNotEndOFSentencesSet.add("NW");	        // northwest
        dotNotEndOFSentencesSet.add("P.S");	    // At	the	end	of	a	letter	or
        dotNotEndOFSentencesSet.add("PA");	        // Personal	Assistant
        dotNotEndOFSentencesSet.add("PM");	        // pm
        dotNotEndOFSentencesSet.add("pt");	        // pint
        dotNotEndOFSentencesSet.add("qt");	        // quart
        dotNotEndOFSentencesSet.add("R.S.V.P");	//	Répondez,	s'il	vous	plait,	this	initialism	comes
        dotNotEndOFSentencesSet.add("Rd");	        //	Road
        dotNotEndOFSentencesSet.add("ROFL");	    // rolling	on	the	floor	laughing
        dotNotEndOFSentencesSet.add("S");	        // south
        dotNotEndOFSentencesSet.add("SE");	        // southeast
        dotNotEndOFSentencesSet.add("SSA");	    // The	Social	Security	Administration.	The	Social	Security
        dotNotEndOFSentencesSet.add("St");	        // Street
        dotNotEndOFSentencesSet.add("SVP");	    // Senior	Vice	President
        dotNotEndOFSentencesSet.add("SW");	        //southwest
        dotNotEndOFSentencesSet.add("tbs");	    // tbsp	or	T	-	tablespoon/tablespoons
        dotNotEndOFSentencesSet.add("tel");	    // telephone
        dotNotEndOFSentencesSet.add("temp");	    // temperature	or	temporary
        dotNotEndOFSentencesSet.add("tsp");	    // or	t	-	teaspoon/teaspoons
        dotNotEndOFSentencesSet.add("TY");	        // thank	you
        dotNotEndOFSentencesSet.add("U.S");	    // United	states
        dotNotEndOFSentencesSet.add("vet");	    // veteran	or	veterinarian
        dotNotEndOFSentencesSet.add("viz");	    //
        // Another	Latin	abbreviation	you	may	see	is
        dotNotEndOFSentencesSet.add("VP");	        // Vice	President
        dotNotEndOFSentencesSet.add("vs");	        // versus
        dotNotEndOFSentencesSet.add("W");	        // west
        dotNotEndOFSentencesSet.add("WC");	        // wrong	conversation

        return dotNotEndOFSentencesSet;
    }



    final static Logger log = LogManager.getLogger(AdjustTextFiles.class);
}
