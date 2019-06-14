import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


public class AdjustTextFilesTest {

    @BeforeClass
    public static void beforeAllTestMethods() {
        o = new AdjustTextFiles();
    }


    @Test
    public void addBeginEndMarkers() {

        String expected = ""; String line = ""; String ret = "";
        // punto non seguito da new line o spazio
        // cioè nel mezzo di una parola

        // --- temp - test here things not working

        // --- end temp

        String s = AdjustTextFiles.startM(null);
        String e = AdjustTextFiles.endM(null);
        String es = AdjustTextFiles.endStartM(null);

        line = "";
        expected = s+" "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = ".";
        expected = s+" "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a";
        expected = s+" a "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a a";
        expected = "sssdd a a eeedd";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a a.";
        expected = "sssdd a a eeedd";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        line = "a. A";
        expected = "sssdd a eeedd sssdd A eeedd";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        // simple detection of abbreviations
        line = "a sr. a";
        expected = "sssdd a sr. a eeedd";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. a";
        expected = s+" sr. a "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr.";
        expected = s+" sr. "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "a .";
        expected = s+" a  "+e;  // punto sparisce ma si inserisce spazio
        // a destra come sempre
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        line = "sr. jr.";
        expected = s+" sr. jr. "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. . jr.";
        expected = s+" sr. . jr. "+e;  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "sr. . jr. .";
        expected = s+" sr. . jr.  "+e;  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. . jr.";
        expected = s+" sr. . jr. "+e;  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        // crazy .

        line = "...";
        expected = s+" ... "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "..";
        expected = s+" .. "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        // simple nearly normal
        line = "Ciao. Ciao";
        expected = s+" Ciao "+AdjustTextFiles.endStartM(null)+" Ciao "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "Ciao. Ciao";
        expected = s+" Ciao "+es+" Ciao "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "notes that Reps. Moran as well ... Not that I take attendance.\"";
        expected = s+" notes that Reps "+es+" Moran as well ... Not that I take attendance\" "+e;
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);



    }

    static AdjustTextFiles o;
}