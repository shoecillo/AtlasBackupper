package com.sh.atlas.app.controller;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sh.atlas.app.dto.BackupDTO;
import com.sh.atlas.app.dto.ConfigDTO;
import com.sh.atlas.app.services.Backupper;

@RestController
public class AtlasController {
	
	@Autowired
	private Backupper backupper;
	
	@GetMapping("/list")
	public List<BackupDTO> listBackups(){
		
		List<Path> ls = backupper.listBackups();
		ls.sort(new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				
				return o1.compareTo(o2);
			}
		});
		return ls.stream().map(it->backupper.converter(it)).collect(Collectors.toList());
	}
	
	@GetMapping("/runBackup")
	public void runBackup() {
		backupper.runBackup();
	}
	
	@GetMapping("/getConfig")
	public ConfigDTO  getConfig() {
		return  backupper.getProperties();
	}
	
}
