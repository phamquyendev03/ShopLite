package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqUserDTO;
import com.quyen.shoplite.domain.response.ResUserDTO;
import com.quyen.shoplite.service.UserService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ApiMessage("Tạo người dùng thành công")
    public ResponseEntity<ResUserDTO> create(@RequestBody ReqUserDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin người dùng")
    public ResponseEntity<ResUserDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    @ApiMessage("Danh sách người dùng")
    public ResponseEntity<List<ResUserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật người dùng thành công")
    public ResponseEntity<ResUserDTO> update(@PathVariable Integer id, @RequestBody ReqUserDTO req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xoá người dùng thành công")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
