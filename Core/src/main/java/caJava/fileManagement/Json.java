package caJava.fileManagement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Для чтения и записи Object
 */
public class Json {

    /**
     * @param object - объект для записи, требуются анотации и класс Views
     */
    static public void write(Object object,File file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
       // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // Convert Object file to JSON string
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }
    }
    static public String write(Object object) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // Convert Object file to JSON string
        try {
            Writer writer = new StringWriter();
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer,object);
            writer.flush();
            return writer.toString();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param _class - тип объекта для записи.
     *
     */
    static public <T> Object readValue(Class<T> _class,File file) throws IOException {
        Object object = null;
        ObjectMapper mapper = new ObjectMapper();
       // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        object = mapper.readValue(file, _class);
        return object;
    }
    static public <T> Object readValue(Class<T> _class,String input) throws IOException {
        Object object = null;
        ObjectMapper mapper = new ObjectMapper();
        // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        object = mapper.readValue(input, _class);
        return object;
    }
}