package sd.user.mapper;

public interface BaseMapper<DTO,Entity>{

    DTO entityToDto(Entity entity);

    Entity DtoToEntity(DTO dto);

}
