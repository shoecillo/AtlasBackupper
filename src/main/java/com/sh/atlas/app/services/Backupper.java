package com.sh.atlas.app.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sh.atlas.app.dto.BackupDTO;
import com.sh.atlas.app.dto.ConfigDTO;

@Component
public class Backupper {

	private static Logger LOGGER = LoggerFactory.getLogger(Backupper.class);
	
	@Value("${atlas.save.path}")
	private String atlasPath;
	
	@Value("${atlas.backups.path}")
	private String backupPath;
	
	@Value("${backup.count}")
	private int maxBackups;
	
	@Value("${backup.delay}")
	private String cronBackup;
	
	@Value("${clean.delay}")
	private String cronClean;
	
	@Autowired
	private Boolean enabledScheduling;
	
	private final static String REGEX_FOLDER = "[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}_[0-9]{2}-[0-9]{10,}$";

	
	@Scheduled(cron="${backup.delay}")
	private void runBackup() {
		if(enabledScheduling) {
			try {
				backup(false);
			} catch (IOException e) {
				LOGGER.error("Error:",e);
			}
		}
	}
	
	@Scheduled(cron="${clean.delay}")
	private void clean() {
		
		if(enabledScheduling) {
			List<Path> ls = listBackups();
			if(ls.size()>maxBackups) {
				ls.sort(new Comparator<Path>() {
					@Override
					public int compare(Path o1, Path o2) {
						
						return o1.compareTo(o2);
					}
				});
				
				List<Path> subLs = ls.subList(0, ls.size()-maxBackups);
				
				subLs.stream().forEach(it->{
					try {
						deleteFolder(it);
					} catch (IOException e) {
						LOGGER.error("Error: ",e);
					}
				});
	
			}
		}
	}
	
	
	public boolean backup(Boolean isManual) throws IOException {
		
		Path atlas = Paths.get(atlasPath);
		DateTime dt = new DateTime();
		DateTimeFormatter fmt =DateTimeFormat.forPattern("YYYY.MM.dd.HH_mm");
		String folderName = "{0}-{1}";		
		folderName = MessageFormat.format(folderName,fmt.print(dt),String.valueOf(dt.getMillis()));
		LOGGER.debug(folderName);
		if(checkBackup(folderName)) {
			
			String newDir = "";
			if(isManual) {
				newDir = backupPath.concat("\\MANUAL\\").concat(folderName).concat("\\SavedAtlasLocal");
			}else {
				newDir = backupPath.concat("\\").concat(folderName).concat("\\SavedAtlasLocal");
			}
			
			Path newDirPath = Paths.get(newDir);
			Files.createDirectories(newDirPath);

			Files.walkFileTree(atlas, new SimpleFileVisitor<Path>() {
					
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						
					LOGGER.debug(file.toString());
					String destPath = newDirPath.toString().concat("\\").concat(file.getFileName().toString());
					LOGGER.debug(destPath);
					Path dest = Paths.get(destPath);
					Files.copy(file, dest, StandardCopyOption.COPY_ATTRIBUTES);
					return FileVisitResult.CONTINUE;
				}
				
			});
			return true;
		} else {
			return false;
		}
	}
		
	
	private boolean checkBackup(String folderName) throws IOException {
		
		
		Path backUpFolder = Paths.get(backupPath);
		String[] names = folderName.split("-");
		
		Pattern pat = Pattern.compile(REGEX_FOLDER);
		List<Path> findPaths = new ArrayList<Path>();
		
		Files.walkFileTree(backUpFolder, new SimpleFileVisitor<Path>(){
		
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				
				if(dir.getFileName().equals(backUpFolder.getFileName())) {
					return FileVisitResult.CONTINUE;
				} else {
					
					Matcher mat = pat.matcher(dir.getFileName().toString());
					if(mat.matches()) {
						String[] fld = dir.getFileName().toString().split("-");
						if(fld[0].equals(names[0])) {
							findPaths.add(dir);
							return FileVisitResult.TERMINATE;
						}
					}else {					
						return FileVisitResult.SKIP_SUBTREE;
					}
				}
				
				return FileVisitResult.SKIP_SUBTREE;
				
			}
			
		});
		
		return findPaths.isEmpty();
	}
	
	
	
	public BackupDTO converter(Path p) {
		
		String[] chunks = p.getFileName().toString().split("-");
		BackupDTO dto = new BackupDTO();
		DateTime dt = new DateTime(Long.valueOf(chunks[1]));
		dto.setDate(dt.toDate());
		dto.setPath(p.toString());
		dto.setTimestamp(Long.valueOf(chunks[1]));
		dto.setName(p.getFileName().toString());
		return dto;
	}
	
	public List<Path> listBackups() {
		
		Path backUpFolder = Paths.get(backupPath);
		File[] dirs = backUpFolder.toFile().listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				Pattern pat = Pattern.compile(REGEX_FOLDER);
				return pathname.isDirectory() && pat.matcher(pathname.getName()).matches();
				
			}
		});
		return Arrays.stream(dirs)
				.map(it->Paths.get(it.getAbsolutePath()))
				.collect(Collectors.toList());
	}
	
	public List<Path> listBackupsManual() {
		
		Path backUpFolder = Paths.get(backupPath.concat("\\MANUAL\\"));
		File[] dirs = backUpFolder.toFile().listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				Pattern pat = Pattern.compile(REGEX_FOLDER);
				return pathname.isDirectory() && pat.matcher(pathname.getName()).matches();
				
			}
		});
		return Arrays.stream(dirs)
				.map(it->Paths.get(it.getAbsolutePath()))
				.collect(Collectors.toList());
	}
	
	private void deleteFolder(Path dir) throws IOException{
		
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
			
		});
	}
	
	public ConfigDTO getProperties() {
		ConfigDTO dto =  new ConfigDTO();
		dto.setAtlasPath(atlasPath);
		dto.setBackupPath(backupPath);
		dto.setCronBackup(cronBackup);
		dto.setCronClean(cronClean);
		dto.setMaxBackups(maxBackups);
		dto.setEnableSchedule(enabledScheduling);
		return dto;
	}
	
	public void enableSchedule(Boolean enable) {
		enabledScheduling = enable;
	}
	
	public Boolean isEnabled() {
		return enabledScheduling;
	}
	
}
