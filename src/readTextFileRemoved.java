import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class readTextFileRemoved {


    static String corpusDir = null;

    static String findDir(String s ) {return null;}
    static String addBeginEndMarkers(String s ) {return null;}
    static boolean isCharToRemove(char a, int b) { return true;}


    // --------------------------------------------------------------
    public static void addBeginEndSentenceMarkers(String fnameIn, String fnameOut) {
        // --------------------------------------------------------------

        String fpathIn = null;  String fpathOut = null;
        if ((corpusDir = findDir(corpusDir)) == null) {
            log.error("directory not found");
            return;
        } else {
            fpathIn   = corpusDir + "\\" + fnameIn;
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
    public static void findNonPrintables(String fname, Map<Integer,Integer> unsualCharsFound) {
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


    // --------------------------------------------------------------
    static boolean hasNonPrintableChar(@NotNull String s, long lineNum, Map<Integer,Integer> charsFound) {
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




    final static Logger log = LogManager.getLogger(readTextFileRemoved.class);


}
