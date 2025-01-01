package com.example.security_app.service;

import com.example.security_app.model.RoleEntity;
import com.example.security_app.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepo roleRepo;

    public ResponseEntity<String> addRole(RoleEntity roleEntity){
      try{
          RoleEntity saveRole = roleRepo.save(roleEntity);
          if (saveRole.getId() > 0){
              return ResponseEntity.status(HttpStatus.CREATED)
                      .body("Role has created");
          }else{
              return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                      .body("Something wrong happened");
          }
      }catch (Exception ex){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Server error please check");
      }
    }

    public List<RoleEntity> getAllRoles(){
        return this.roleRepo.findAll();
    }
}
