package pl.poszkole.PoSzkole.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AttendanceServiceUnitTest.class,
        AuthenticationServiceUnitTest.class,
        ClassScheduleServiceUnitTest.class,
        CourseServiceUnitTest.class,
        RequestServiceUnitTest.class,
        RoomReservationServiceUnitTest.class,
        ScheduleChangesLogServiceUnitTest.class,
        SubjectServiceUnitTest.class,
        WebsiteUserServiceUnitTest.class,
})
public class AllUnitTestsSuite {
}
