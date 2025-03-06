package sd.user.mapper;

import org.springframework.stereotype.Component;
import sd.user.dto.RoleDTO;
import sd.user.model.Role;

import java.util.List;

@Component
public class RoleMapper implements BaseMapper<RoleDTO, Role> {
    @Override
    public RoleDTO entityToDto(Role role) {
        return RoleDTO.builder()
                .role(role.getRole())
                .build();
    }

    public List<RoleDTO> roleListEntityToDto(List<Role> roles){
        return roles.stream()
                .map(this::entityToDto)
                .toList();
    }

    @Override
    public Role DtoToEntity(RoleDTO roleDTO) {
        return Role.builder()
                .role(roleDTO.getRole())
                .build();
    }

    public List<Role> roleListDtoToEntity(List<RoleDTO> roleDTOList){
        return roleDTOList.stream()
                .map(this::DtoToEntity)
                .toList();
    }
}
