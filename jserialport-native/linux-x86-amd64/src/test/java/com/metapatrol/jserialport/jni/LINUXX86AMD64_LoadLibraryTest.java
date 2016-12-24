package com.metapatrol.jserialport.jni;

import org.junit.Test;

import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class LINUXX86AMD64_LoadLibraryTest {

    @Test
    public void test_library_can_be_loaded() throws Exception {
        URL resource = this.getClass().getResource("/lib/linux-x86-amd64/libjserialport.so");
        Runtime.getRuntime().load(resource.getFile());
    }
}
