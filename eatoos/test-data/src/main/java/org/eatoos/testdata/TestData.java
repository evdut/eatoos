package org.eatoos.testdata;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class TestData 
{
    public static void main( String[] args ) throws IOException
    {
        new Output(new InputData("src/main/resources/config.yaml")).generate("generated/");
    }
}
