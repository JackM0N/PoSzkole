package pl.poszkole.PoSzkole.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AttendanceServiceIntegrationTest.class,
        AuthenticationServiceIntegrationTest.class,
        ClassScheduleServiceIntegrationTest.class,
        CourseServiceIntegrationTest.class,
        RequestServiceIntegrationTest.class,
        RoomReservationServiceIntegrationTest.class,
        ScheduleChangesLogServiceIntegrationTest.class,
        SubjectServiceIntegrationTest.class,
        WebsiteUserServiceIntegrationTest.class,
})
public class AllIntegrationTestsSuite {
}
