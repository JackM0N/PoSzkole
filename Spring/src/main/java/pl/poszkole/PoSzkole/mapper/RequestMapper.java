package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.model.Request;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "acceptanceDate", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "issueDate", expression = "java(java.time.LocalDate.now())")
    Request toEntity(RequestDTO requestDTO);

    RequestDTO toDto(Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Request partialUpdate(RequestDTO requestDTO, @MappingTarget Request request);
}