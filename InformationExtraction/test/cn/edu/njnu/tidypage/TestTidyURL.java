package cn.edu.njnu.tidypage;

import org.junit.Test;

import java.util.Random;

public class TestTidyURL {

    @Test
    public void testURLTidy() {
        int item = new Random(System.currentTimeMillis()).nextInt(4);
        System.out.println(item);
        System.out.print(new TidyPage("").tidyURL("incubator.pkusp.com.cn"));
    }

}
