package com.sh.atlas.app.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sh.atlas.app.dto.BackupDTO;
import com.sh.atlas.app.dto.ConfigDTO;
import com.sh.atlas.app.exceptions.BackupExistsException;
import com.sh.atlas.app.services.Backupper;

@RestController
public class AtlasController {
	
	@Autowired
	private Backupper backupper;
	

	
	@GetMapping("/list")
	public ResponseEntity<List<BackupDTO>> listBackups(){
		
		List<Path> ls = backupper.listBackups();
		ls.sort(new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				
				return o1.compareTo(o2);
			}
		});
		return new ResponseEntity<List<BackupDTO>>(
				ls.stream().map(it->backupper.converter(it)).collect(Collectors.toList()),
				HttpStatus.OK);
	}
	
	@GetMapping("/listManual")
	public ResponseEntity<List<BackupDTO>> listBackupsManual(){
		
		List<Path> ls = backupper.listBackupsManual();
		ls.sort(new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				
				return o1.compareTo(o2);
			}
		});
		return new ResponseEntity<List<BackupDTO>>(
				ls.stream().map(it->backupper.converter(it)).collect(Collectors.toList()),
				HttpStatus.OK);
	}
	
	
	@GetMapping("/runBackup")
	public Boolean runBackup() throws IOException, BackupExistsException {
		if(backupper.backup(true)) {
			return true;
		}else {
			throw new BackupExistsException("You cant Backup again in the same minute.");
		}
	}
	
	@GetMapping("/getConfig")
	public ResponseEntity<ConfigDTO>  getConfig() {
		
		return new ResponseEntity<ConfigDTO>(backupper.getProperties(),
				HttpStatus.OK);
	}
	
	@GetMapping("/enableSchedule")
	public void enableSchedule(@RequestParam("enable") Boolean enable) {
		
		backupper.enableSchedule(enable);
		
	}
	
	@GetMapping("/isEnabled")
	public ResponseEntity<Boolean> isEnableSchedule(){
		
		return new ResponseEntity<Boolean>(backupper.isEnabled(),
				HttpStatus.OK);
		
	}
	
}
