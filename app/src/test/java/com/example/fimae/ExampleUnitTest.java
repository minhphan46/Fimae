package com.example.fimae;

import static com.example.fimae.utils.StringUtils.removeAccent;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.fimae.utils.StringUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    //Write test case for remove Vietnamese accent
    @Test
    public  void removeVietnameseAccent_isCorrect(){
        assertEquals(removeAccent("Nguyễn Văn A"), "Nguyen Van A");
        assertEquals(removeAccent("Nguyễn Văn B"), "Nguyen Van B");
        assertEquals(removeAccent("A á ấ ấ"), "A a a a");
        assertEquals("aeiou", removeAccent("áéíóú"));
        assertEquals("hello", removeAccent("hello"));
        assertEquals("", removeAccent(""));
    }
}