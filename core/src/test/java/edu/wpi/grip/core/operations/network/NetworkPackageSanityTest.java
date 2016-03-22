package edu.wpi.grip.core.operations.network;


import com.google.common.testing.AbstractPackageSanityTests;

public class NetworkPackageSanityTest extends AbstractPackageSanityTests {
    public NetworkPackageSanityTest() {
        super();
        ignoreClasses(c -> c.equals(MockNetworkPublisher.class));
        publicApiOnly();
    }
}
