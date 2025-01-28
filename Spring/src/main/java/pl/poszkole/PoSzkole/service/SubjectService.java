package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.mapper.SubjectMapper;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final WebsiteUserService websiteUserService;

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream().map(subjectMapper::toDto).collect(Collectors.toList());
    }

    public List<SubjectDTO> getCurrentTeacherSubjects() {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        return currentUser.getSubjects().stream().map(subjectMapper::toDto).collect(Collectors.toList());
    }
}
