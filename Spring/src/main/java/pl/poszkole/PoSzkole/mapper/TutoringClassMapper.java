package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.model.TutoringClass;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SimplifiedUserMapper.class})
public interface TutoringClassMapper {
    TutoringClass toEntity(TutoringClassDTO tutoringClassDTO);

    @Mapping(source = "teacher", target = "teacher")
    TutoringClassDTO toDto(TutoringClass tutoringClass);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TutoringClass partialUpdate(TutoringClassDTO tutoringClassDTO, @MappingTarget TutoringClass tutoringClass);
}