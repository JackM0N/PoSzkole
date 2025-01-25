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
        TutoringClassServiceIntegrationTest.class,
        UserBusyDayServiceIntegrationTest.class,
        WebsiteUserServiceIntegrationTest.class,
        AttendanceServiceUnitTest.class,
        AuthenticationServiceUnitTest.class,
        ClassScheduleServiceUnitTest.class,
        CourseServiceUnitTest.class,
        RequestServiceUnitTest.class,
        RoomReservationServiceUnitTest.class,
        ScheduleChangesLogServiceUnitTest.class,
        SubjectServiceUnitTest.class,
        TutoringClassServiceUnitTest.class,
        UserBusyDayServiceUnitTest.class,
        WebsiteUserServiceUnitTest.class,
})
public class AllTestsSuite {
    // yes, this should be empty
}