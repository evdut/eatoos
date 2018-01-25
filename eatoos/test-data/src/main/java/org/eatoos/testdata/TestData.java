package org.eatoos.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

/**
 * Hello world!
 *
 */
public class TestData 
{
    public static void main( String[] args ) throws FileNotFoundException
    {
        InputStream input = new FileInputStream(new File("src/test/resources/reader/utf-8.txt"));
        Yaml yaml = new Yaml();
        Object data = yaml.load(input);
    }
}
