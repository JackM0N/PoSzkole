package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.model.Request;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "admissionDate", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Request toEntity(RequestDTO requestDTO);

    RequestDTO toDto(Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Request partialUpdate(RequestDTO requestDTO, @MappingTarget Request request);
}