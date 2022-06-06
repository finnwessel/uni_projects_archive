package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.domain.dto.UserResponse;
import de.hsflensburg.authservice.domain.mapper.UserMapper;
import de.hsflensburg.authservice.domain.model.RoleEnum;
import de.hsflensburg.authservice.service.UserService.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Search")
@RestController
@RequestMapping("search")
@RequiredArgsConstructor
public class SearchController {
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUserWithRole(@RequestParam RoleEnum role, String name) {
        List<UserResponse> userResponseList = userService.searchUserWithRole(role, name).stream().map(UserMapper.INSTANCE::userToUserResponse).toList();
        return ResponseEntity.ok().body(userResponseList);
    }
}
