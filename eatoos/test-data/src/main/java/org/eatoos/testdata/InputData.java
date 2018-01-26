package org.eatoos.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class InputData {
    
    Map<String, Object> data;
    
    @SuppressWarnings("unchecked")
	public InputData(String filePath) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File(filePath));
        Yaml yaml = new Yaml();
        data = (Map<String, Object>)yaml.load(input);
    }

    @SuppressWarnings("unchecked")
	public <E> E fetch(String key, Class<E> clazz) {
        return (E)data.get(key);
    }
}
