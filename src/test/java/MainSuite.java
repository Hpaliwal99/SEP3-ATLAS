import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class MainSuite {

    @Test
    public void maintest() {
        int x = new Random().nextInt(5);
        assertEquals(3, x);
    }
}
