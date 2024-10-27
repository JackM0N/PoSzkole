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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBusyDayService {
    private final UserBusyDayRepository userBusyDayRepository;
    private final WebsiteUserRepository websiteUserRepository;
    private final UserBusyDayMapper userBusyDayMapper;

    public List<UserBusyDayDTO> getUserBusyDays(Long userId) {
        WebsiteUser websiteUser = websiteUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userBusyDayRepository.findByUserId(websiteUser.getId())
                .stream().map(userBusyDayMapper::toDto).collect(Collectors.toList());
    }

    public UserBusyDayDTO createUserBusyDay(UserBusyDayDTO userBusyDayDTO) {
        WebsiteUser websiteUser = websiteUserRepository.findById(userBusyDayDTO.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (isOverlapping(websiteUser, userBusyDayDTO)) {
            throw new RuntimeException("Chosen schedule is overlapping with already existing one");
        }

        UserBusyDay userBusyDay = userBusyDayMapper.toEntity(userBusyDayDTO);
        userBusyDay.setUser(websiteUser);

        return userBusyDayMapper.toDto(userBusyDayRepository.save(userBusyDay));
    }

    public UserBusyDayDTO updateUserBusyDay(Long bdId, UserBusyDayDTO userBusyDayDTO) {
        UserBusyDay userBusyDay = userBusyDayRepository.findById(bdId)
                .orElseThrow(() -> new EntityNotFoundException("User schedule not found"));
        if (isOverlapping(userBusyDay.getUser(), userBusyDayDTO)) {
            throw new RuntimeException("Chosen schedule is overlapping with already existing one");
        }
        userBusyDayMapper.partialUpdate(userBusyDayDTO, userBusyDay);
        return userBusyDayMapper.toDto(userBusyDayRepository.save(userBusyDay));
    }

    public void deleteUserBusyDay(Long bdId) {
        //TODO: Check if current user can make this operation
        userBusyDayRepository.deleteById(bdId);
    }

    //TODO: Could be probably used in site itself to add in-real time info if given time is wrong for chosen day
    private boolean isOverlapping(WebsiteUser websiteUser, UserBusyDayDTO userBusyDayDTO) {
        List<UserBusyDay> userBusyDays = userBusyDayRepository.findByUserId(websiteUser.getId());
        boolean timeFromOk;
        boolean timeToOk;
        boolean completeOverlap;

        for (UserBusyDay userBusyDay : userBusyDays) {
            if (Objects.equals(userBusyDay.getDayOfTheWeek(), userBusyDayDTO.getDayOfTheWeek().toString())) {
                //Check if time_from is not between existing time_from and time_to
                timeFromOk = userBusyDayDTO.getTimeFrom().isAfter(userBusyDay.getTimeTo())
                        || userBusyDayDTO.getTimeFrom().isBefore(userBusyDay.getTimeFrom());

                //Check if time_to is not between existing time_from and time_to
                timeToOk = userBusyDayDTO.getTimeTo().isAfter(userBusyDay.getTimeTo())
                        || userBusyDayDTO.getTimeTo().isBefore(userBusyDay.getTimeFrom());

                //Check if it's not a duplicate or is extended version of existing time frame
                completeOverlap = ((userBusyDayDTO.getTimeFrom().isBefore(userBusyDay.getTimeFrom())
                                || userBusyDayDTO.getTimeFrom().equals(userBusyDay.getTimeFrom()))
                                &&
                                (userBusyDayDTO.getTimeTo().isAfter(userBusyDay.getTimeTo())
                                || userBusyDayDTO.getTimeTo().equals(userBusyDay.getTimeTo())));

                if (!timeFromOk || !timeToOk || completeOverlap) {
                    return true;
                }
            }
        }
        return false;
    }
}
