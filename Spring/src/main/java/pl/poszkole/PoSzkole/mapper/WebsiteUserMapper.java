package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.model.WebsiteUser;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);
}