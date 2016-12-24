package com.metapatrol.jserialport;

import org.junit.Test;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class LibraryLoadingTest {

    @Test
    public void test_library_can_be_loaded(){
        LibraryLoader.load();
    }
}
