package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.model.WebsiteUser;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    @Mapping(target = "classes", ignore = true)
    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "hourlyRate", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "issueInvoice", ignore = true)
    @Mapping(target = "priceListId", ignore = true)
    @Mapping(target = "classes", ignore = true)
    WebsiteUserDTO toDtoWithoutSensitiveData(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);
}