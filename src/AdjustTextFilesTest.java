import org.junit.BeforeClass;

import static org.junit.Assert.*;


public class AdjustTextFilesTest {

    @BeforeClass
    public static void beforeAllTestMethods() {
        o = new AdjustTextFiles();
    }


    @org.junit.Test
    public void addBeginEndMarkers() {

        String expected = ""; String line = ""; String ret = "";
        // punto non seguito da new line o spazio
        // cioè nel mezzo di una parola

        // --- temp - test here things not working

        // --- end temp
        line = "a";
        expected = "sss a eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "a a";
        expected = "sss a a eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "a a.";
        expected = "sss a a eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);


        line = "a. A";
        expected = "sss a. eee sss A eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        // simple detection of abbreviations
        line = "a sr. a";
        expected = "sss a sr. a eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "sr. a";
        expected = "sss sr. a eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "sr.";
        expected = "sss sr. eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "sr.";
        expected = "sss sr. eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "sr. jr.";
        expected = "sss sr. jr. eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);

        line = "a. A";
        expected = "sss a. eee sss A eee";
        ret = AdjustTextFiles.addBeginEndMarkersNew(line);
        assertEquals(expected, ret);
    }

    static AdjustTextFiles o;
}