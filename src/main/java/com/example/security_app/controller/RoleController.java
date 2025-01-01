package com.example.security_app.controller;

import com.example.security_app.model.RoleEntity;
import com.example.security_app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    public ResponseEntity<String> addRole(@RequestBody RoleEntity roleEntity){
        return this.roleService.addRole(roleEntity);
    }
    @GetMapping("/allRoles")
    public List<RoleEntity> getAllRoles(){
        return this.roleService.getAllRoles();
    }
}
