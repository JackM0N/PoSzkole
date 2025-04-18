package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.UserBusyDayDTO;
import pl.poszkole.PoSzkole.mapper.UserBusyDayMapper;
import pl.poszkole.PoSzkole.model.UserBusyDay;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.UserBusyDayRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBusyDayService {
    private final UserBusyDayRepository userBusyDayRepository;
    private final WebsiteUserRepository websiteUserRepository;
    private final UserBusyDayMapper userBusyDayMapper;
    private final AuthorizationService authorizationService;

    public List<UserBusyDayDTO> getUserBusyDays(Long userId) {
        WebsiteUser websiteUser = websiteUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userBusyDayRepository.findByUserIdOrderByTimeTo(websiteUser.getId())
                .stream().map(userBusyDayMapper::toDto).collect(Collectors.toList());
    }

    public UserBusyDayDTO createUserBusyDay(UserBusyDayDTO userBusyDayDTO) {
        WebsiteUser websiteUser = websiteUserRepository.findById(userBusyDayDTO.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //Check if created schedule is overlapping
        if (isOverlapping(websiteUser, null, userBusyDayDTO.getDayOfTheWeek(),
                userBusyDayDTO.getTimeFrom(), userBusyDayDTO.getTimeTo())) {
            throw new RuntimeException("Chosen schedule is overlapping with already existing one");
        }

        //Create userBusyDay
        UserBusyDay userBusyDay = userBusyDayMapper.toEntity(userBusyDayDTO);
        userBusyDay.setUser(websiteUser);

        //Check if this user can perform this action (author or manager if for example mother calls for schedule change)
        if (authorizationService.cantModifyEntity(userBusyDay)){
            throw new RuntimeException("You do not have permission to create this schedule");
        }

        return userBusyDayMapper.toDto(userBusyDayRepository.save(userBusyDay));
    }

    public UserBusyDayDTO updateUserBusyDay(Long bdId, UserBusyDayDTO userBusyDayDTO) {
        //Find chosen userBusyDay
        UserBusyDay userBusyDay = userBusyDayRepository.findById(bdId)
                .orElseThrow(() -> new EntityNotFoundException("User schedule not found"));

        //Check to see if this user can perform this action
        if (authorizationService.cantModifyEntity(userBusyDay)){
            throw new RuntimeException("You do not have permission to edit this schedule");
        }

        //Get wanted user
        WebsiteUser websiteUser = websiteUserRepository.findById(userBusyDay.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //Check for overlapping
        if (isOverlapping(websiteUser, userBusyDayDTO.getId(), userBusyDayDTO.getDayOfTheWeek(),
                userBusyDayDTO.getTimeFrom(), userBusyDayDTO.getTimeTo())) {
            throw new RuntimeException("Chosen schedule is overlapping with already existing one");
        }

        //Update
        userBusyDayMapper.partialUpdate(userBusyDayDTO, userBusyDay);
        return userBusyDayMapper.toDto(userBusyDayRepository.save(userBusyDay));
    }

    public void deleteUserBusyDay(Long bdId) {
        UserBusyDay userBusyDay = userBusyDayRepository.findById(bdId)
                .orElseThrow(() -> new EntityNotFoundException("User schedule not found"));

        if (authorizationService.cantModifyEntity(userBusyDay)){
            throw new RuntimeException("You do not have permission to delete this schedule");
        }

        userBusyDayRepository.deleteById(bdId);
    }


    public boolean isOverlapping(WebsiteUser websiteUser, Long ubdId, DayOfWeek dayOfWeek, LocalTime timeFrom, LocalTime timeTo) {
        List<UserBusyDay> userBusyDays = userBusyDayRepository.findByUserIdOrderByTimeTo(websiteUser.getId());
        boolean timeFromOk;
        boolean timeToOk;
        boolean completeOverlap;

        for (UserBusyDay userBusyDay : userBusyDays) {
            //Ignore checking against busyDay that's being edited
            if(ubdId != null && ubdId.equals(userBusyDay.getId())) {
                continue;
            }

            if (Objects.equals(userBusyDay.getDayOfTheWeek(), dayOfWeek.toString())) {
                //Check if time_from is not between existing time_from and time_to
                timeFromOk = timeFrom.isAfter(userBusyDay.getTimeTo())
                        || timeFrom.isBefore(userBusyDay.getTimeFrom());

                //Check if time_to is not between existing time_from and time_to
                timeToOk = timeTo.isAfter(userBusyDay.getTimeTo())|| timeTo.isBefore(userBusyDay.getTimeFrom());

                //Check if it's not a duplicate or is extended version of existing time frame
                completeOverlap = ((timeFrom.isBefore(userBusyDay.getTimeFrom())
                        || timeFrom.equals(userBusyDay.getTimeFrom()))
                        &&
                        (timeTo.isAfter(userBusyDay.getTimeTo())
                        || timeTo.equals(userBusyDay.getTimeTo())));

                if (!timeFromOk || !timeToOk || completeOverlap) {
                    return true;
                }
            }
        }
        return false;
    }
}
