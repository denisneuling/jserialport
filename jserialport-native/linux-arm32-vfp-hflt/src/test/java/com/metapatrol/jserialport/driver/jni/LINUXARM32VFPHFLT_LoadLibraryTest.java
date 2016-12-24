package com.metapatrol.jserialport.driver.jni;

import org.junit.Test;
import org.junit.Ignore;

import java.net.URL;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Ignore
public class LINUXARM32VFPHFLT_LoadLibraryTest {

    @Test
    public void test_library_can_be_loaded() throws Exception {
        URL resource = this.getClass().getResource("/lib/linux-arm32-vfp-hflt/libjserialport.so");
        Runtime.getRuntime().load(resource.getFile());
    }
}
