package it.polimi.rsp.test.mock.vocals;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.test.mock.MockInputClass;
import lombok.ToString;
import org.junit.Test;

import static it.polimi.rsp.vocals.annotations.VocalsUtils.serialize;


public class SerializerTest {


    public static void main(String[] args) {

        System.out.println(serialize(new JsonObject(), "id", String.class));
        System.out.println(serialize(new JsonObject(), "", MockInputClass.class).toString());

    }

    @Test
    public void test() {

        String p = "{\"name\":\"Ricardo\", \"age\":27}";
        String a = "{\"race\":\"Doberman\", \"age\":15}";

        Gson gson = new Gson();
        Person person = gson.fromJson(p, Person.class);
        Animal animal = gson.fromJson(a, Animal.class);

        System.out.println(person);
        System.out.println(animal);

        String d = "{{\"name\":\"Ricardo\", \"age\":27}, {\"race\":\"Doberman\", \"age\":15}}";

        person = gson.fromJson(d, Person.class);
        animal = gson.fromJson(d, Animal.class);

        System.out.println(person);
        System.out.println(animal);

        String c = "{\"name\":\"Ricardo\", \"race\":\"Doberman\", \"age\":15}";

        person = gson.fromJson(c, Person.class);
        animal = gson.fromJson(c, Animal.class);

        System.out.println(person);
        System.out.println(animal);

    }


    @ToString
    class Person {
        public String name;
        public Integer age;
    }

    @ToString
    class Animal {
        public String race;
        public Integer age;
    }

}
