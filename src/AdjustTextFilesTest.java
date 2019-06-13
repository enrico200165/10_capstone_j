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

        line = "";
        expected = "sss eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = ".";
        expected = "sss eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a";
        expected = "sss a eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a a";
        expected = "sss a a eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "a a.";
        expected = "sss a a eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        line = "a. A";
        expected = "sss a eee sss A eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        // simple detection of abbreviations
        line = "a sr. a";
        expected = "sss a sr. a eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. a";
        expected = "sss sr. a eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr.";
        expected = "sss sr. eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "a .";
        expected = "sss a  eee";  // punto sparisce ma si inserisce spazio
        // a destra come sempre
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        line = "sr. jr.";
        expected = "sss sr. jr. eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. . jr.";
        expected = "sss sr. . jr. eee";  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "sr. . jr. .";
        expected = "sss sr. . jr.  eee";  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "sr. . jr.";
        expected = "sss sr. . jr. eee";  // niente maiuscola dopo uno spazio
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);


        // crazy .

        line = "...";
        expected = "sss ... eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "..";
        expected = "sss .. eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        // simple nearly normal
        line = "Ciao. Ciao";
        expected = "sss Ciao eee sss Ciao eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line = "Ciao. Ciao";
        expected = "sss Ciao eee sss Ciao eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);

        line =         "notes that Reps. Moran as well ... Not that I take attendance.\"";
        expected = "sss notes that Reps eee sss Moran as well ... Not that I take attendance\" eee";
        ret = AdjustTextFiles.addBeginEndMarkers(line);
        assertEquals(expected, ret);



    }

    static AdjustTextFiles o;
}