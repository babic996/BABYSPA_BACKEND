package com.backend.babyspa.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.models.Role;
import com.backend.babyspa.v1.services.RoleService;
import com.backend.babyspa.v1.utils.ApiResponse;

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

	@Autowired
	RoleService roleService;

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<List<Role>>> findAll() {

		try {
			return createSuccessResponse(roleService.findAll());
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}
}
