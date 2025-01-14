package pl.poszkole.PoSzkole.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AttendanceServiceUnitTest.class,
    AttendanceServiceIntegrationTest.class,
    WebsiteUserServiceUnitTest.class,
    WebsiteUserServiceIntegrationTest.class,
})
public class AllTestsSuite {
    // yes, this should be empty
}