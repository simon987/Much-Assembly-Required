package net.simon987.cubotplugin;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CubotTest {

    @Test
    public void test(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, 1);
    }
}